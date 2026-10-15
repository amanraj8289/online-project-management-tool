/**
 * ProManage Enterprise - Chart.js Visualization Integrations
 */

// Initialize Task Status Doughnut Chart
function initTaskStatusChart(canvasId, statusData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    const labels = Object.keys(statusData);
    const dataValues = Object.values(statusData);

    const backgroundColors = [
        '#94a3b8', // TODO - Gray
        '#4f46e5', // IN_PROGRESS - Indigo
        '#f59e0b', // IN_REVIEW - Amber
        '#10b981'  // COMPLETED - Emerald
    ];

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: dataValues,
                backgroundColor: backgroundColors,
                borderWidth: 2,
                borderColor: '#ffffff',
                hoverOffset: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        boxWidth: 12,
                        font: { size: 12, family: 'Inter' }
                    }
                }
            },
            cutout: '70%'
        }
    });
}

// Initialize Project Progress Bar Chart
function initProjectProgressBarChart(canvasId, progressList) {
    const ctx = document.getElementById(canvasId);
    if (!ctx || !progressList || progressList.length === 0) return;

    const labels = progressList.map(p => p.projectTitle.length > 20 ? p.projectTitle.substring(0, 20) + '...' : p.projectTitle);
    const completedTasks = progressList.map(p => p.completedTasks);
    const inProgressTasks = progressList.map(p => p.inProgressTasks);
    const todoTasks = progressList.map(p => p.todoTasks);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Completed',
                    data: completedTasks,
                    backgroundColor: '#10b981'
                },
                {
                    label: 'In Progress',
                    data: inProgressTasks,
                    backgroundColor: '#4f46e5'
                },
                {
                    label: 'To Do / Review',
                    data: todoTasks,
                    backgroundColor: '#cbd5e1'
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    stacked: true,
                    grid: { display: false }
                },
                y: {
                    stacked: true,
                    beginAtZero: true,
                    ticks: { precision: 0 }
                }
            },
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { boxWidth: 12 }
                }
            }
        }
    });
}
