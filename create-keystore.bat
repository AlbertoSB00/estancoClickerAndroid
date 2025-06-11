@echo off
echo Creando keystore para Fumadero Tycoon...

"%JAVA_HOME%\bin\keytool.exe" -genkey -v -keystore fumadero-upload-key.keystore -alias fumadero -keyalg RSA -keysize 2048 -validity 10000 -storepass fumadero123 -keypass fumadero123 -dname "CN=Fumadero Tycoon, OU=Game Development, O=Alberto SB, L=Madrid, S=Madrid, C=ES"

echo Keystore creado exitosamente!
pause
