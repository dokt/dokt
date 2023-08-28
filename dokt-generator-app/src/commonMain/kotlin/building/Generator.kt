package app.dokt.generator.building

interface Generator<M : Any, T> : Writer<M, T> {
    fun createModel() : M

    fun generate() {
        val model = createModel()
        debug { "Created ${model.log} and now generating it." }
        model.generateModel()
        model.write()
    }

    fun M.generateModel()
}
