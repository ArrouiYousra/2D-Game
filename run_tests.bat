@echo off
REM Script Windows pour lancer facilement les tests unitaires
REM Usage: run_tests.bat

echo 🧪 Lancement des tests unitaires...
echo.

REM Lancer les tests
call gradlew.bat :core:test --info

REM Vérifier le code de retour
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Tous les tests sont passés !
    echo.
    echo 📊 Rapport HTML disponible dans: core\build\reports\tests\test\index.html
    echo 📄 Rapport XML disponible dans: core\build\test-results\test\
) else (
    echo.
    echo ❌ Certains tests ont échoué. Consultez le rapport pour plus de détails.
    exit /b 1
)

