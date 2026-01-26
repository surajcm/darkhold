var teamData = [];

function toOptions() {
    document.forms[0].action = "/options";
    document.forms[0].submit();
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
            showGreeting(JSON.parse(greeting.body).users);
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
        })
        .catch(err => console.error('Error loading teams:', err));
}

function renderTeamView() {
    let teamColumns = document.getElementById('teamColumns');
    if (!teamColumns) return;

    teamColumns.innerHTML = '';
    teamData.forEach(team => {
        let column = document.createElement('div');
        column.className = 'team-column team-column-' + team.color;
        column.innerHTML = `
            <div class="team-column-header">
                <h5 class="team-column-title">
                    <span class="team-color-dot team-color-dot-${team.color}"></span>
                    ${escapeHtmlPlayer(team.name)}
                </h5>
                <div class="team-column-count">${(team.members || []).length} members</div>
            </div>
            <div class="team-members-list">
                ${(team.members || []).map(m => `
                    <div class="team-member-item">
                        <span>${escapeHtmlPlayer(m)}</span>
                    </div>
                `).join('')}
            </div>
        `;
        teamColumns.appendChild(column);
    });
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