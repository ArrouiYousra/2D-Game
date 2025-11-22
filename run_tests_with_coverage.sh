#!/bin/bash

# Script pour lancer les tests avec génération du rapport de couverture
# Usage: ./run_tests_with_coverage.sh

echo "🧪 Lancement des tests avec couverture de code..."
echo ""

# Lancer les tests et générer le rapport de couverture
./gradlew :core:test :core:jacocoTestReport

# Vérifier le code de retour
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Tests terminés avec succès !"
    echo ""
    echo "📊 Rapport de couverture HTML disponible dans: core/build/reports/jacoco/test/html/index.html"
    echo "📄 Rapport de couverture XML disponible dans: core/build/reports/jacoco/test/jacocoTestReport.xml"
    echo "📈 Rapport de tests HTML disponible dans: core/build/reports/tests/test/index.html"
    echo ""
    echo "💡 Pour ouvrir le rapport de couverture:"
    echo "   xdg-open core/build/reports/jacoco/test/html/index.html"
else
    echo ""
    echo "❌ Erreur lors de l'exécution des tests."
    exit 1
fi

