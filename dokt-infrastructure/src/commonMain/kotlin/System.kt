package app.dokt.infra

//#region Operating system
expect val osArch: String?
expect val osName: String?
expect val osVer: String?
expect val linux: Boolean
expect val mac: Boolean
expect val windows: Boolean
//#endregion

expect val hostname: String?
expect val java: Int?
expect val username: String
expect val userDir: String

