#!/bin/bash

# check_quality_gate.sh
# Usage: ./check_quality_gate.sh <service_name>
# Example: ./check_quality_gate.sh product

SERVICE=$1

if [ -z "$SERVICE" ]; then
    echo "Error: Service name is required."
    exit 1
fi

if [ ! -d "$SERVICE" ]; then
    echo "Error: Directory $SERVICE does not exist."
    exit 1
fi

COVERAGE=0
REPORT_FOUND=false

if [ -f "$SERVICE/pom.xml" ]; then
    # Java service - check Jacoco CSV report
    JACOCO_CSV="$SERVICE/target/site/jacoco/jacoco.csv"
    if [ -f "$JACOCO_CSV" ]; then
        REPORT_FOUND=true
        # Jacoco CSV format: GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED
        # We will sum INSTRUCTION_MISSED (col 4) and INSTRUCTION_COVERED (col 5)
        
        MISSED=$(awk -F',' '{if (NR>1) sum+=$4} END {print sum}' "$JACOCO_CSV")
        COVERED=$(awk -F',' '{if (NR>1) sum+=$5} END {print sum}' "$JACOCO_CSV")
        
        TOTAL=$((MISSED + COVERED))
        
        if [ "$TOTAL" -eq 0 ]; then
            COVERAGE=0
        else
            # Calculate percentage with 2 decimal places using awk
            COVERAGE=$(awk "BEGIN {print ($COVERED / $TOTAL) * 100}")
        fi
    else
        echo "Jacoco report not found at $JACOCO_CSV"
    fi

elif [ -f "$SERVICE/package.json" ]; then
    # Node.js service - check Jest coverage-summary.json
    JEST_SUMMARY="$SERVICE/coverage/coverage-summary.json"
    if [ -f "$JEST_SUMMARY" ]; then
        REPORT_FOUND=true
        # Parse total line coverage percentage from JSON using jq
        # jq '.total.lines.pct' path/to/summary
        if command -v jq &> /dev/null; then
            COVERAGE=$(jq -r '.total.lines.pct' "$JEST_SUMMARY")
            if [ "$COVERAGE" == "Unknown" ] || [ -z "$COVERAGE" ]; then
                COVERAGE=0
            fi
        else
            echo "jq is not installed, cannot parse Jest summary."
            # Fallback using grep/sed. Get only the first 'lines' total which is the global coverage.
            COVERAGE=$(grep -A 5 '"total":' "$JEST_SUMMARY" | grep '"lines":' | grep -o '"pct":[0-9.]*' | cut -d':' -f2 | head -n 1)
            if [ -z "$COVERAGE" ]; then
                COVERAGE=0
            fi
        fi
    else
        echo "Jest summary report not found at $JEST_SUMMARY"
    fi
else
    echo "Unknown project type for service: $SERVICE"
    exit 1
fi

if [ "$REPORT_FOUND" = false ]; then
    echo "No coverage report found. Quality Gate failed."
    exit 1
fi

echo "Coverage for $SERVICE is $COVERAGE%"

# Compare coverage using awk since it might be a float
PASSED=$(awk "BEGIN {if ($COVERAGE > 70.0) print 1; else print 0}")

if [ "$PASSED" -eq 1 ]; then
    echo "Quality Gate PASSED: Coverage is > 70%"
    exit 0
else
    echo "Quality Gate FAILED: Coverage is <= 70%"
    exit 1
fi
