/** Greeter unit tests */
class GreeterTest : GreeterSpec({ // GreeterSpec is generated in previous step.
    greet { // Command handler is a generated test context
        test("World") { // "World" test case
            greeter // Greeter is arranged with a random UUID.
                .act { greet("World") } // The command act
                .emits(Greeted("Hello, World!")) // Asserts the emitted DTO.
        }
    }
})
