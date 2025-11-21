#!/bin/bash

# Pipeline automatique pour compiler, tester et générer les rapports
# Usage: ./pipeline.sh

set -e  # Arrêter en cas d'erreur

echo "🚀 Démarrage du pipeline de build et tests..."
echo ""

# Étape 1: Nettoyer le projet
echo "🧹 Nettoyage du projet..."
./gradlew clean
echo "✅ Nettoyage terminé"
echo ""

# Étape 2: Compiler le projet
echo "🔨 Compilation du projet..."
./gradlew :core:compileJava
if [ $? -ne 0 ]; then
    echo "❌ Erreur de compilation"
    exit 1
fi
echo "✅ Compilation réussie"
echo ""

# Étape 3: Exécuter les tests
echo "🧪 Exécution des tests unitaires..."
./gradlew :core:test
if [ $? -ne 0 ]; then
    echo "❌ Des tests ont échoué"
    exit 1
fi
echo "✅ Tous les tests sont passés"
echo ""

# Étape 4: Générer le rapport de couverture
echo "📊 Génération du rapport de couverture..."
./gradlew :core:jacocoTestReport
if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la génération du rapport de couverture"
    exit 1
fi
echo "✅ Rapport de couverture généré"
echo ""

# Étape 5: Générer la JavaDoc
echo "📖 Génération de la JavaDoc..."
./gradlew :core:javadoc
if [ $? -ne 0 ]; then
    echo "⚠️  Avertissement: Erreur lors de la génération de la JavaDoc (non bloquant)"
fi
echo "✅ JavaDoc générée"
echo ""

# Résumé
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Pipeline terminé avec succès !"
echo ""
echo "📊 Rapports disponibles:"
echo "   • Tests: core/build/reports/tests/test/index.html"
echo "   • Couverture: core/build/reports/jacoco/test/html/index.html"
echo "   • JavaDoc: core/build/docs/javadoc/index.html"
echo ""
echo "💡 Pour ouvrir les rapports:"
echo "   xdg-open core/build/reports/jacoco/test/html/index.html"
echo "   xdg-open core/build/reports/tests/test/index.html"
echo "   xdg-open core/build/docs/javadoc/index.html"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

