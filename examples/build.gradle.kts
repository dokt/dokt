description = "Dokt examples"

applicationProject("window-simulator") {
    commonMain {
        implementation("app.dokt:dokt-application:_")
    }
    jvmMain {
        implementation("net.java.dev.jna:jna-platform:_")
    }
}
