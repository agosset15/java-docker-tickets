$ErrorActionPreference = "Stop"
$baseUrl = "http://localhost:8080"

Write-Host "1. Проверка backend через nginx"
$info = Invoke-RestMethod "$baseUrl/api/info"
$info | ConvertTo-Json

Write-Host "2. Получение демонстрационных сегментов"
$segments = Invoke-RestMethod "$baseUrl/api/segments?ticketNumber=5552139265672"
if ($segments.Count -ne 2) {
    throw "Ожидалось 2 сегмента, получено $($segments.Count)"
}

Write-Host "3. Проверка страницы frontend"
$page = Invoke-WebRequest "$baseUrl/"
if ($page.StatusCode -ne 200 -or $page.Content -notmatch "Java Tickets") {
    throw "Frontend не ответил ожидаемой страницей"
}

Write-Host "Smoke test пройден успешно" -ForegroundColor Green
