#!/bin/bash

# Script pour lancer facilement les tests unitaires
# Usage: ./run_tests.sh

echo "🧪 Lancement des tests unitaires..."
echo ""

# Lancer les tests
./gradlew :core:test --info

# Vérifier le code de retour
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Tous les tests sont passés !"
    echo ""
    echo "📊 Rapport HTML disponible dans: core/build/reports/tests/test/index.html"
    echo "📄 Rapport XML disponible dans: core/build/test-results/test/"
else
    echo ""
    echo "❌ Certains tests ont échoué. Consultez le rapport pour plus de détails."
    exit 1
fi

