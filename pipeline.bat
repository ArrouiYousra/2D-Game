@echo off
REM Pipeline automatique pour compiler, tester et générer les rapports (Windows)
REM Usage: pipeline.bat

setlocal enabledelayedexpansion

echo 🚀 Démarrage du pipeline de build et tests...
echo.

REM Étape 1: Nettoyer le projet
echo 🧹 Nettoyage du projet...
call gradlew.bat clean
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Erreur lors du nettoyage
    exit /b 1
)
echo ✅ Nettoyage terminé
echo.

REM Étape 2: Compiler le projet
echo 🔨 Compilation du projet...
call gradlew.bat :core:compileJava
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Erreur de compilation
    exit /b 1
)
echo ✅ Compilation réussie
echo.

REM Étape 3: Exécuter les tests
echo 🧪 Exécution des tests unitaires...
call gradlew.bat :core:test
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Des tests ont échoué
    exit /b 1
)
echo ✅ Tous les tests sont passés
echo.

REM Étape 4: Générer le rapport de couverture
echo 📊 Génération du rapport de couverture...
call gradlew.bat :core:jacocoTestReport
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Erreur lors de la génération du rapport de couverture
    exit /b 1
)
echo ✅ Rapport de couverture généré
echo.

REM Étape 5: Générer la JavaDoc
echo 📖 Génération de la JavaDoc...
call gradlew.bat :core:javadoc
if %ERRORLEVEL% NEQ 0 (
    echo ⚠️  Avertissement: Erreur lors de la génération de la JavaDoc (non bloquant)
)
echo ✅ JavaDoc générée
echo.

REM Résumé
echo ════════════════════════════════════════════════════════════════════════════════════
echo ✅ Pipeline terminé avec succès !
echo.
echo 📊 Rapports disponibles:
echo    • Tests: core\build\reports\tests\test\index.html
echo    • Couverture: core\build\reports\jacoco\test\html\index.html
echo    • JavaDoc: core\build\docs\javadoc\index.html
echo.
echo 💡 Pour ouvrir les rapports:
echo    start core\build\reports\jacoco\test\html\index.html
echo    start core\build\reports\tests\test\index.html
echo    start core\build\docs\javadoc\index.html
echo ════════════════════════════════════════════════════════════════════════════════════

