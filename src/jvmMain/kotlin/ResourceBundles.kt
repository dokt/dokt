package app.dokt

import java.util.ResourceBundle

fun ResourceBundle.find(clazz: Class<*>) = find(clazz.simpleName)

fun ResourceBundle.find(key: String) = if (containsKey(key)) getString(key) else null

fun ResourceBundle.get(clazz: Class<*>, default: String = clazz.simpleName) = get(clazz.simpleName, default)

fun ResourceBundle.get(key: String, default: String = key): String = if (containsKey(key)) getString(key) else default
