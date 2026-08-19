$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "=== Android Dev Launcher ==="
Write-Host ""

# ------------------------------------------------------------
# 1. Localizar Android SDK
# ------------------------------------------------------------

if ($env:ANDROID_HOME) {
    $SdkPath = $env:ANDROID_HOME
}
elseif ($env:ANDROID_SDK_ROOT) {
    $SdkPath = $env:ANDROID_SDK_ROOT
}
else {
    $SdkPath = Join-Path $env:LOCALAPPDATA "Android\Sdk"
}

$Emulator = Join-Path $SdkPath "emulator\emulator.exe"
$Adb      = Join-Path $SdkPath "platform-tools\adb.exe"

if (!(Test-Path $Emulator)) {
    throw "No se encontro Android Emulator en: $Emulator"
}

if (!(Test-Path $Adb)) {
    throw "No se encontro adb en: $Adb"
}

Write-Host "SDK: $SdkPath"

# ------------------------------------------------------------
# 2. Verificar proyecto Android
# ------------------------------------------------------------

if (!(Test-Path ".\gradlew.bat")) {
    throw "Ejecuta el script desde la raiz del proyecto Android."
}

# ------------------------------------------------------------
# 3. Detectar applicationId
# ------------------------------------------------------------

$GradleFiles = @(
    ".\app\build.gradle.kts",
    ".\app\build.gradle"
)

$GradleFile = $GradleFiles |
    Where-Object { Test-Path $_ } |
    Select-Object -First 1

if (!$GradleFile) {
    throw "No se encontro app/build.gradle.kts ni app/build.gradle."
}

$GradleContent = Get-Content $GradleFile -Raw

$ApplicationId = $null

# Kotlin DSL:
# applicationId = "com.example.app"
#
# Groovy:
# applicationId "com.example.app"

if ($GradleContent -match 'applicationId\s*(?:=\s*)?["'']([^"'']+)["'']') {
    $ApplicationId = $Matches[1]
}

# Fallback a namespace
if (!$ApplicationId) {
    if ($GradleContent -match 'namespace\s*(?:=\s*)?["'']([^"'']+)["'']') {
        $ApplicationId = $Matches[1]

        Write-Warning "No se encontro applicationId. Usando namespace: $ApplicationId"
    }
}

if (!$ApplicationId) {
    throw "No fue posible detectar applicationId."
}

# ------------------------------------------------------------
# 4. Detectar applicationIdSuffix de debug
# ------------------------------------------------------------

$DebugApplicationId = $ApplicationId

if ($GradleContent -match '(?s)debug\s*\{.*?applicationIdSuffix\s*(?:=\s*)?["'']([^"'']+)["'']') {

    $Suffix = $Matches[1]

    $DebugApplicationId = "$ApplicationId$Suffix"
}

Write-Host "Package: $DebugApplicationId"

# ------------------------------------------------------------
# 5. Verificar si ya existe un emulador ejecutandose
# ------------------------------------------------------------

& $Adb start-server | Out-Null

$RunningEmulators = & $Adb devices |
    Select-String '^emulator-\d+\s+device'

$DeviceSerial = $null

if ($RunningEmulators) {

    $FirstRunning = $RunningEmulators |
        Select-Object -First 1

    $DeviceSerial = (
        $FirstRunning.ToString() -split '\s+'
    )[0]

    Write-Host "Emulador ya iniciado: $DeviceSerial"
}
else {

    # --------------------------------------------------------
    # 6. Detectar primer AVD disponible
    # --------------------------------------------------------

    $Avds = @(& $Emulator -list-avds) |
        Where-Object { ![string]::IsNullOrWhiteSpace($_) }

    if ($Avds.Count -eq 0) {
        throw "No hay ningun AVD configurado."
    }

    $AvdName = $Avds[0]

    Write-Host "AVD detectado: $AvdName"
    Write-Host "Iniciando emulador..."

    Start-Process `
        -FilePath $Emulator `
        -ArgumentList @("-avd", $AvdName)

    # --------------------------------------------------------
    # 7. Esperar hasta que aparezca el nuevo emulator-XXXX
    # --------------------------------------------------------

    do {

        Start-Sleep -Seconds 2

        $RunningEmulators = & $Adb devices |
            Select-String '^emulator-\d+\s+device'

    } until ($RunningEmulators)

    $FirstRunning = $RunningEmulators |
        Select-Object -First 1

    $DeviceSerial = (
        $FirstRunning.ToString() -split '\s+'
    )[0]

    Write-Host "Emulador conectado: $DeviceSerial"
}

# ------------------------------------------------------------
# 8. Esperar boot completo
# ------------------------------------------------------------

Write-Host "Esperando que Android termine de iniciar..."

& $Adb -s $DeviceSerial wait-for-device

do {

    Start-Sleep -Seconds 2

    try {

        $BootCompleted = (
            & $Adb `
                -s $DeviceSerial `
                shell getprop sys.boot_completed `
                2>$null
        ).Trim()

    }
    catch {
        $BootCompleted = ""
    }

    Write-Host "." -NoNewline

} until ($BootCompleted -eq "1")

Write-Host ""
Write-Host "Android listo."

# ------------------------------------------------------------
# 9. Desbloquear pantalla
# ------------------------------------------------------------

& $Adb -s $DeviceSerial shell input keyevent 82 | Out-Null

# ------------------------------------------------------------
# 10. Compilar + instalar Debug
# ------------------------------------------------------------

Write-Host ""
Write-Host "Compilando e instalando aplicacion..."

& .\gradlew.bat installDebug

if ($LASTEXITCODE -ne 0) {
    throw "Gradle installDebug fallo."
}

Write-Host "Aplicacion instalada."

# ------------------------------------------------------------
# 11. Verificar instalacion
# ------------------------------------------------------------

$InstalledPackage = & $Adb `
    -s $DeviceSerial `
    shell pm list packages $DebugApplicationId

if (!$InstalledPackage) {
    throw "El package $DebugApplicationId no aparece instalado."
}

# ------------------------------------------------------------
# 12. Detener instancia anterior de la app
# ------------------------------------------------------------

& $Adb `
    -s $DeviceSerial `
    shell am force-stop $DebugApplicationId

# ------------------------------------------------------------
# 13. Abrir aplicacion
# ------------------------------------------------------------

Write-Host "Abriendo $DebugApplicationId..."

& $Adb `
    -s $DeviceSerial `
    shell monkey `
    -p $DebugApplicationId `
    -c android.intent.category.LAUNCHER `
    1 | Out-Null

Write-Host ""
Write-Host "=============================="
Write-Host " Android listo"
Write-Host " Device : $DeviceSerial"
Write-Host " Package: $DebugApplicationId"
Write-Host "=============================="
Write-Host ""