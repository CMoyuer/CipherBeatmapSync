rd /s /Q "www"
cd CiphermapSync_HTML && npm run build && cd .. && xcopy /e "CiphermapSync_HTML\dist\" "www\" && cordova run && pause