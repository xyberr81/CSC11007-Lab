param (
    [Parameter(Mandatory=$true)]
    [string]$Service
)

if (-not (Test-Path $Service)) {
    Write-Error "Error: Directory '$Service' does not exist."
    exit 1
}

$Coverage = 0
$ReportFound = $false

$PomPath = Join-Path $Service "pom.xml"
$PackageJsonPath = Join-Path $Service "package.json"

if (Test-Path $PomPath) {
    # Java service - check Jacoco CSV report
    $JacocoCsv = Join-Path $Service "target\site\jacoco\jacoco.csv"
    if (Test-Path $JacocoCsv) {
        $ReportFound = $true
        # Parse CSV
        $CsvData = Import-Csv $JacocoCsv
        
        $Missed = 0
        $Covered = 0
        
        foreach ($Row in $CsvData) {
            $Missed += [int]$Row.INSTRUCTION_MISSED
            $Covered += [int]$Row.INSTRUCTION_COVERED
        }
        
        $Total = $Missed + $Covered
        if ($Total -eq 0) {
            $Coverage = 0
        } else {
            $Coverage = [math]::Round(($Covered / $Total) * 100, 2)
        }
    } else {
        Write-Host "Jacoco report not found at $JacocoCsv"
    }
} elseif (Test-Path $PackageJsonPath) {
    # Node.js service - check Jest coverage-summary.json
    $JestSummary = Join-Path $Service "coverage\coverage-summary.json"
    if (Test-Path $JestSummary) {
        $ReportFound = $true
        # Parse JSON
        $JsonData = Get-Content $JestSummary | ConvertFrom-Json
        $Coverage = $JsonData.total.lines.pct
        
        if ($null -eq $Coverage -or $Coverage -eq "Unknown") {
            $Coverage = 0
        }
    } else {
        Write-Host "Jest summary report not found at $JestSummary"
    }
} else {
    Write-Error "Unknown project type for service: $Service"
    exit 1
}

if (-not $ReportFound) {
    Write-Error "No coverage report found. Quality Gate failed."
    exit 1
}

Write-Host "Coverage for $Service is $Coverage%"

if ($Coverage -gt 70) {
    Write-Host "Quality Gate PASSED: Coverage is > 70%" -ForegroundColor Green
    exit 0
} else {
    Write-Host "Quality Gate FAILED: Coverage is <= 70%" -ForegroundColor Red
    exit 1
}
