var teamData = [];
var allPlayers = [];

function startGame() {
    let pin = document.getElementById('quizPin').value;
    console.log('triggering quiz with pin ' + pin);
    document.getElementById('quiz_pin').value = pin;
    let socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        // Send PIN directly for PIN-scoped topic
        stompClient.send("/app/start", {}, pin);
        // Subscribe to PIN-scoped topic for concurrent game support
        stompClient.subscribe('/topic/' + pin + '/start', function (greeting) {
            gotoMyGame();
        });
    });
}

function gotoMyGame() {
    document.forms[0].action = "/interstitial";
    document.forms[0].submit();
}

function logOut() {
    document.forms[0].action = "/logout";
    document.forms[0].submit();
}

function toOptions() {
    document.forms[0].action = "/options";
    document.forms[0].submit();
}

function toHome() {
    document.forms[0].method = 'get';
    document.forms[0].action = "/";
    document.forms[0].submit();
}

function connect() {
    let pin = document.getElementById('quizPin').value;
    let teamMode = document.getElementById('teamMode')?.value === 'true';
    let socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        // Subscribe to PIN-scoped topic for concurrent game support
        stompClient.subscribe('/topic/' + pin + '/user', function (greeting) {
            let users = JSON.parse(greeting.body).users;
            allPlayers = users;
            showGreeting(users);
            if (teamMode) {
                loadTeamsAndRender();
            }
        });

        // Subscribe to team updates if in team mode
        if (teamMode) {
            stompClient.subscribe('/topic/' + pin + '/team_update', function (message) {
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
        })
        .catch(err => console.error('Error loading teams:', err));
}

function renderTeamView() {
    let teamColumns = document.getElementById('teamColumns');
    let unassignedDiv = document.getElementById('unassignedPlayers');

    if (!teamColumns || !unassignedDiv) return;

    // Get all assigned players
    let assignedPlayers = new Set();
    teamData.forEach(team => {
        (team.members || []).forEach(m => assignedPlayers.add(m));
    });

    // Find unassigned players (excluding moderator)
    let moderator = document.getElementById('user')?.textContent || '';
    let unassigned = allPlayers.filter(p => !assignedPlayers.has(p) && p !== moderator);

    // Render unassigned players
    unassignedDiv.innerHTML = '';
    if (unassigned.length === 0) {
        unassignedDiv.innerHTML = '<span class="text-muted">All players assigned</span>';
    } else {
        unassigned.forEach(player => {
            let badge = document.createElement('div');
            badge.className = 'team-member-item';
            badge.innerHTML = `
                <span>${escapeHtml(player)}</span>
                <select class="team-assign-dropdown" onchange="assignPlayerToTeam('${escapeHtml(player)}', this.value)">
                    <option value="">Assign to...</option>
                    ${teamData.map(t => `<option value="${escapeHtml(t.name)}">${escapeHtml(t.name)}</option>`).join('')}
                </select>
            `;
            unassignedDiv.appendChild(badge);
        });
    }

    // Render team columns
    teamColumns.innerHTML = '';
    teamData.forEach(team => {
        let column = document.createElement('div');
        column.className = 'team-column team-column-' + team.color;
        column.innerHTML = `
            <div class="team-column-header">
                <h5 class="team-column-title">
                    <span class="team-color-dot team-color-dot-${team.color}"></span>
                    ${escapeHtml(team.name)}
                </h5>
                <div class="team-column-count">${(team.members || []).length} members</div>
            </div>
            <div class="team-members-list">
                ${(team.members || []).map(m => `
                    <div class="team-member-item">
                        <span>${escapeHtml(m)}</span>
                        <button type="button" class="btn btn-sm btn-outline-danger" onclick="removeFromTeam('${escapeHtml(m)}')">
                            &times;
                        </button>
                    </div>
                `).join('')}
            </div>
        `;
        teamColumns.appendChild(column);
    });
}

function assignPlayerToTeam(username, teamName) {
    if (!teamName) return;

    let pin = document.getElementById('quizPin').value;
    fetch('/team/assign', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pin: pin, username: username, teamName: teamName })
    })
    .then(response => {
        if (response.ok) {
            loadTeamsAndRender();
        } else {
            alert('Failed to assign player to team');
        }
    })
    .catch(err => console.error('Error assigning player:', err));
}

function removeFromTeam(username) {
    // Removing a player means assigning them to no team
    // For now, we'll just refresh - backend would need unassign endpoint
    loadTeamsAndRender();
}

function autoAssignAllPlayers() {
    let pin = document.getElementById('quizPin').value;
    let assignmentMethod = document.getElementById('assignmentMethod')?.value || 'BALANCED';

    // Get unassigned players
    let assignedPlayers = new Set();
    teamData.forEach(team => {
        (team.members || []).forEach(m => assignedPlayers.add(m));
    });

    let moderator = document.getElementById('user')?.textContent || '';
    let unassigned = allPlayers.filter(p => !assignedPlayers.has(p) && p !== moderator);

    if (unassigned.length === 0) {
        alert('All players are already assigned to teams');
        return;
    }

    // Auto-assign each unassigned player
    let promises = unassigned.map((player, index) => {
        let teamIndex;
        if (assignmentMethod === 'RANDOM') {
            teamIndex = Math.floor(Math.random() * teamData.length);
        } else {
            // BALANCED: assign to team with fewest members
            let minCount = Math.min(...teamData.map(t => (t.members || []).length));
            teamIndex = teamData.findIndex(t => (t.members || []).length === minCount);
            // Update local count for next iteration
            teamData[teamIndex].members = teamData[teamIndex].members || [];
            teamData[teamIndex].members.push(player);
        }

        let teamName = teamData[teamIndex].name;
        return fetch('/team/assign', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ pin: pin, username: player, teamName: teamName })
        });
    });

    Promise.all(promises)
        .then(() => loadTeamsAndRender())
        .catch(err => console.error('Error auto-assigning:', err));
}

function escapeHtml(text) {
    if (!text) return '';
    let div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showGreeting(message) {
    console.log(message);
    let tableRef = document.getElementById('conversation').getElementsByTagName('tbody')[0];
    tableRef.innerHTML = "";
    for (let i = 0; i < message.length; i++) {
        let newRow = tableRef.insertRow(tableRef.rows.length);
        newRow.innerHTML = message[i];
    }
}