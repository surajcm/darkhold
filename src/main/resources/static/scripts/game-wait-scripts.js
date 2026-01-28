var teamData = [];
var allUsers = [];
var draggedPlayer = null;

function toOptions() {
    document.forms[0].action = "/options";
    document.forms[0].submit();
}

function isModerator() {
    let currentUser = document.getElementById('name').value;
    let moderator = document.getElementById('moderator').value;
    return currentUser === moderator;
}

function isManualAssignment() {
    let method = document.getElementById('assignmentMethod')?.value;
    return method === 'MANUAL';
}

function connect() {
    let name_val = document.getElementById('name').value;
    let pin_val = document.getElementById('quizPin').value;
    let teamMode = document.getElementById('teamMode')?.value === 'true';
    let socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.send("/app/user", {}, JSON.stringify({'name': name_val, 'pin': pin_val}));
        // Subscribe to PIN-scoped topics for concurrent game support
        stompClient.subscribe('/topic/' + pin_val + '/user', function (greeting) {
            allUsers = JSON.parse(greeting.body).users;
            showGreeting(allUsers);
            if (teamMode && isManualAssignment()) {
                renderUnassignedPlayers();
            }
        });

        stompClient.subscribe('/topic/' + pin_val + '/start', function (greeting) {
            gotoMyGame();
        });

        // Subscribe to kick events (PIN-scoped)
        stompClient.subscribe('/topic/' + pin_val + '/player_kicked', function (message) {
            let kickedUser = message.body;
            let currentUser = document.getElementById('name').value;
            if (kickedUser === currentUser) {
                alert('You have been removed from this game.');
                window.location.href = '/';
            }
        });

        // Subscribe to team updates if in team mode
        if (teamMode) {
            stompClient.subscribe('/topic/' + pin_val + '/team_update', function (message) {
                console.log('Team update received');
                loadTeamsAndRender();
            });

            // Initial load of teams
            loadTeamsAndRender();
        }
    });
}

function loadTeamsAndRender() {
    let pin = document.getElementById('quizPin').value;
    fetch('/team/list/' + pin)
        .then(response => response.json())
        .then(teams => {
            teamData = teams;
            renderTeamView();
            updateMyTeamDisplay();
            renderUnassignedPlayers();
        })
        .catch(err => console.error('Error loading teams:', err));
}

function renderTeamView() {
    let teamColumns = document.getElementById('teamColumns');
    if (!teamColumns) return;

    let canDrag = isModerator() && isManualAssignment();

    teamColumns.innerHTML = '';
    teamData.forEach(team => {
        let column = document.createElement('div');
        column.className = 'team-column team-column-' + team.color;
        if (canDrag) {
            column.classList.add('drop-zone');
            column.setAttribute('data-team', team.name);
            column.ondragover = handleDragOver;
            column.ondragleave = handleDragLeave;
            column.ondrop = handleDrop;
        }
        column.innerHTML = `
            <div class="team-column-header">
                <h5 class="team-column-title">
                    <span class="team-color-dot team-color-dot-${team.color}"></span>
                    ${escapeHtmlPlayer(team.name)}
                </h5>
                <div class="team-column-count">${(team.members || []).length} members</div>
            </div>
            <div class="team-members-list">
                ${(team.members || []).map(m => renderTeamMember(m, canDrag)).join('')}
            </div>
        `;
        teamColumns.appendChild(column);
    });
}

function renderTeamMember(username, canDrag) {
    if (canDrag) {
        return `
            <div class="team-member-item draggable-player" draggable="true"
                 data-username="${escapeHtmlPlayer(username)}"
                 ondragstart="handleDragStart(event)"
                 ondragend="handleDragEnd(event)">
                <span>${escapeHtmlPlayer(username)}</span>
            </div>
        `;
    }
    return `
        <div class="team-member-item">
            <span>${escapeHtmlPlayer(username)}</span>
        </div>
    `;
}

function renderUnassignedPlayers() {
    let section = document.getElementById('unassignedSection');
    let container = document.getElementById('unassignedPlayers');
    if (!section || !container || !isModerator() || !isManualAssignment()) {
        if (section) section.style.display = 'none';
        return;
    }

    // Find unassigned players
    let assignedPlayers = new Set();
    teamData.forEach(team => {
        (team.members || []).forEach(m => assignedPlayers.add(m));
    });

    let moderator = document.getElementById('moderator').value;
    let unassigned = allUsers.filter(u => !assignedPlayers.has(u) && u !== moderator);

    if (unassigned.length === 0) {
        section.style.display = 'none';
        return;
    }

    section.style.display = 'block';
    container.innerHTML = unassigned.map(username => `
        <div class="unassigned-player draggable-player" draggable="true"
             data-username="${escapeHtmlPlayer(username)}"
             ondragstart="handleDragStart(event)"
             ondragend="handleDragEnd(event)">
            <span>${escapeHtmlPlayer(username)}</span>
        </div>
    `).join('');
}

function handleDragStart(event) {
    draggedPlayer = event.target.getAttribute('data-username');
    event.target.classList.add('dragging');
    event.dataTransfer.effectAllowed = 'move';
    event.dataTransfer.setData('text/plain', draggedPlayer);
}

function handleDragEnd(event) {
    event.target.classList.remove('dragging');
    draggedPlayer = null;
    // Remove all drag-over states
    document.querySelectorAll('.drag-over').forEach(el => el.classList.remove('drag-over'));
}

function handleDragOver(event) {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
    event.currentTarget.classList.add('drag-over');
}

function handleDragLeave(event) {
    event.currentTarget.classList.remove('drag-over');
}

function handleDrop(event) {
    event.preventDefault();
    event.currentTarget.classList.remove('drag-over');

    let teamName = event.currentTarget.getAttribute('data-team');
    let username = event.dataTransfer.getData('text/plain');

    if (username && teamName) {
        assignPlayerToTeam(username, teamName);
    }
}

function assignPlayerToTeam(username, teamName) {
    let pin = document.getElementById('quizPin').value;
    console.log('Assigning', username, 'to team', teamName);
    stompClient.send("/app/team/assign", {}, pin + ':' + username + ':' + teamName);
}

function updateMyTeamDisplay() {
    let currentUser = document.getElementById('name').value;
    let myTeamDisplay = document.getElementById('myTeamDisplay');
    let myTeamBadge = document.getElementById('myTeamBadge');

    if (!myTeamDisplay || !myTeamBadge) return;

    // Find user's team
    let myTeam = null;
    teamData.forEach(team => {
        if ((team.members || []).includes(currentUser)) {
            myTeam = team;
        }
    });

    if (myTeam) {
        myTeamDisplay.style.display = 'block';
        myTeamBadge.textContent = myTeam.name;
        myTeamBadge.className = 'team-badge team-badge-' + myTeam.color;
    } else {
        myTeamDisplay.style.display = 'block';
        myTeamBadge.textContent = 'Not assigned yet';
        myTeamBadge.className = 'team-badge';
        myTeamBadge.style.backgroundColor = '#6c757d';
    }
}

function escapeHtmlPlayer(text) {
    if (!text) return '';
    let div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showGreeting(message) {
    console.log(message);
    let tableRef = document.getElementById('conversation').getElementsByTagName('tbody')[0];
    tableRef.innerHTML = "";
    let moderator = document.getElementById('moderator').value;
    let currentUser = document.getElementById('name').value;
    let isModerator = currentUser === moderator;
    let participantCount = 0;

    for (let i = 0; i < message.length; i++) {
        let username = message[i];
        // Don't count moderator in participant count
        if (username !== moderator) {
            participantCount++;
        }
        let newRow = tableRef.insertRow(tableRef.rows.length);
        let cell = newRow.insertCell(0);
        cell.innerHTML = buildParticipantRow(username, isModerator, moderator);
    }

    // Update participant count display
    let countElem = document.getElementById('participantCount');
    if (countElem) {
        countElem.textContent = participantCount;
    }
}

function buildParticipantRow(username, isModerator, moderator) {
    let html = '<span>' + escapeHtml(username) + '</span>';
    if (username === moderator) {
        html += ' <span class="badge bg-primary">Moderator</span>';
    }
    // Add kick button for moderator (can't kick self)
    if (isModerator && username !== moderator) {
        html += ' <button type="button" class="btn btn-sm btn-danger ms-2" '
            + 'onclick="kickPlayer(\'' + escapeHtml(username) + '\')">Kick</button>';
    }
    return html;
}

function escapeHtml(text) {
    let div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function kickPlayer(username) {
    if (confirm('Are you sure you want to remove ' + username + ' from the game?')) {
        let pin = document.getElementById('quizPin').value;
        stompClient.send("/app/kick_player", {}, pin + ':' + username);
    }
}

function gotoMyGame() {
    document.forms[0].action = "/interstitial";
    document.forms[0].submit();
}

function logOut() {
    document.forms[0].action = "/logout";
    document.forms[0].submit();
}