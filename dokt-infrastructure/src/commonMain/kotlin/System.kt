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


expect val cores: Int

/** The amount of memory in bytes that is available for this application. */
expect val freeMem: Long

/** The maximum amount of memory in bytes that application is limited (if exists) to use. */
expect val maxMem: Long?

/** The total amount of memory in bytes that this application currently uses and what is available. */
expect val totalMem: Long

