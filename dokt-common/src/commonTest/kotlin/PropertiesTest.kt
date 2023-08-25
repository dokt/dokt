package app.dokt.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PropertiesTest : FunSpec({
    // [An example of a properties file](https://en.wikipedia.org/wiki/.properties)
    val example = """
        # You are reading a comment in ".properties" file.
        ! The exclamation mark can also be used for comments.
        # Lines with "properties" contain a key and a value separated by a delimiting character.
        # There are 3 delimiting characters: '=' (equal), ':' (colon) and whitespace (space, \t and \f).
        website = https://en.wikipedia.org/
        language : English
        topic .properties files
        # A word on a line will just create a key with no value.
        empty
        # White space that appears between the key, the value and the delimiter is ignored.
        # This means that the following are equivalent (other than for readability).
        hello=hello
        hello = hello
        # Keys with the same name will be overwritten by the key that is the furthest in a file.
        # For example the final value for "duplicateKey" will be "second".
        duplicateKey = first
        duplicateKey = second
        # To use the delimiter characters inside a key, you need to escape them with a \.
        # However, there is no need to do this in the value.
        delimiterCharacters\:\=\ = This is the value for the key "delimiterCharacters\:\=\ "
        # Adding a \ at the end of a line means that the value continues to the next line.
        multiline = This line \
        continues
        # If you want your value to include a \, it should be escaped by another \.
        path = c:\\wiki\\templates
        # This means that if the number of \ at the end of the line is even, the next line is not included in the value. 
        # In the following example, the value for "evenKey" is "This is on one line\".
        evenKey = This is on one line\\
        # This line is a normal comment and is not included in the value for "evenKey"
        # If the number of \ is odd, then the next line is included in the value.
        # In the following example, the value for "oddKey" is "This is line one and\#This is line two".
        oddKey = This is line one and\\\
        # This is line two
        # White space characters are removed before each line.
        # Make sure to add your spaces before your \ if you need them on the next line.
        # In the following example, the value for "welcome" is "Welcome to Wikipedia!".
        welcome = Welcome to \
                  Wikipedia!
        # If you need to add newlines and carriage returns, they need to be escaped using \n and \r respectively.
        # You can also optionally escape tabs with \t for readability purposes.
        valueWithEscapes = This is a newline\n and a carriage return\r and a tab\t.
        # You can also use Unicode escape characters (maximum of four hexadecimal digits).
        # In the following example, the value for "encodedHelloInJapanese" is "こんにちは".
        encodedHelloInJapanese = \u3053\u3093\u306b\u3061\u306f
        # But with more modern file encodings like UTF-8, you can directly use supported characters.
        helloInJapanese = こんにちは
    """.trimIndent()

    val output = """
        # You are reading a comment in ".properties" file.
        ! The exclamation mark can also be used for comments.
        # Lines with "properties" contain a key and a value separated by a delimiting character.
        # There are 3 delimiting characters: '=' (equal), ':' (colon) and whitespace (space, \t and \f).
        website = https://en.wikipedia.org/
        language = English
        topic = .properties files
        # A word on a line will just create a key with no value.
        empty
        # White space that appears between the key, the value and the delimiter is ignored.
        # This means that the following are equivalent (other than for readability).
        hello = hello
        # Keys with the same name will be overwritten by the key that is the furthest in a file.
        # For example the final value for "duplicateKey" will be "second".
        duplicateKey = second
        # To use the delimiter characters inside a key, you need to escape them with a \.
        # However, there is no need to do this in the value.
        delimiterCharacters\:\=\  = This is the value for the key "delimiterCharacters:= "
        # Adding a \ at the end of a line means that the value continues to the next line.
        multiline = This line continues
        # If you want your value to include a \, it should be escaped by another \.
        path = c:\\wiki\\templates
        # This means that if the number of \ at the end of the line is even, the next line is not included in the value. 
        # In the following example, the value for "evenKey" is "This is on one line\".
        evenKey = This is on one line\\
        # This line is a normal comment and is not included in the value for "evenKey"
        # If the number of \ is odd, then the next line is included in the value.
        # In the following example, the value for "oddKey" is "This is line one and\#This is line two".
        oddKey = This is line one and\\# This is line two
        # White space characters are removed before each line.
        # Make sure to add your spaces before your \ if you need them on the next line.
        # In the following example, the value for "welcome" is "Welcome to Wikipedia!".
        welcome = Welcome to Wikipedia!
        # If you need to add newlines and carriage returns, they need to be escaped using \n and \r respectively.
        # You can also optionally escape tabs with \t for readability purposes.
        valueWithEscapes = This is a newline\n and a carriage return\r and a tab\t.
        # You can also use Unicode escape characters (maximum of four hexadecimal digits).
        # In the following example, the value for "encodedHelloInJapanese" is "こんにちは".
        encodedHelloInJapanese = こんにちは
        # But with more modern file encodings like UTF-8, you can directly use supported characters.
        helloInJapanese = こんにちは
    """.trimIndent()

    test("parse") {
        PropertyReader(example.lineSequence()).properties().lines().joinLines() shouldBe output
    }

    test("rewrite") {
        PropertyReader(output.lineSequence()).properties().lines().joinLines() shouldBe output
    }
})