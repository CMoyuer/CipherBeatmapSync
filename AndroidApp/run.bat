rd /s /Q "CiphermapSync/www/"
cd CiphermapSync_HTML && npm run build && cd .. && xcopy /e "CiphermapSync_HTML\dist\" "CiphermapSync\www\" && cd CiphermapSync && cordova run && cd .. && pause