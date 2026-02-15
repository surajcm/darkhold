#!/bin/bash
# Translation Helper Script for Darkhold
# This script helps identify which messages need translation

LANG_CODE=$1

if [ -z "$LANG_CODE" ]; then
    echo "Usage: ./translation_helper.sh <language_code>"
    echo "Example: ./translation_helper.sh fr"
    exit 1
fi

ENGLISH_FILE="src/main/resources/messages.properties"
LANG_FILE="src/main/resources/messages_${LANG_CODE}.properties"

if [ ! -f "$ENGLISH_FILE" ]; then
    echo "Error: $ENGLISH_FILE not found"
    exit 1
fi

if [ ! -f "$LANG_FILE" ]; then
    echo "Error: $LANG_FILE not found"
    echo "Create it first by copying messages_es.properties"
    exit 1
fi

# Count messages
TOTAL=$(grep -c "^[a-z].*=" "$ENGLISH_FILE")
TRANSLATED=$(grep -c "^[a-z].*=" "$LANG_FILE")
MISSING=$((TOTAL - TRANSLATED))

echo "======================================"
echo "Translation Status for '$LANG_CODE'"
echo "======================================"
echo "Total messages:      $TOTAL"
echo "Translated:          $TRANSLATED"
echo "Missing:             $MISSING"
echo "Progress:            $((TRANSLATED * 100 / TOTAL))%"
echo "======================================"

if [ $MISSING -gt 0 ]; then
    echo ""
    echo "To complete translations:"
    echo "1. Open $LANG_FILE"
    echo "2. Compare with $ENGLISH_FILE"
    echo "3. Translate missing messages"
    echo "4. Test with: ./gradlew bootRun"
fi
