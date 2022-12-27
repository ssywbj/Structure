package com.suheng.structure.view.kt

//class Empty //类声明由类名、类头（指定其类型参数、主构造函数等）以及由花括号包围的类休构成。类头与类体都是可选的：如果一个类没有类体，可以省略花括号。

/*class Person constructor(firstName: String) { //constructor：类可以有一个主构造函数以及一个或多个次构造函数。主构造函数是类头的一部分：它跟在类名（与可选的类型参数）后
}*/

/*
class Person(firstName: String) { //如果主构造函数没有任何注解或者可见性修饰符，可以省略这个 constructor 关键字
}*/

//如果构造函数有注解或可见性修饰符，constructor关键字是必需的，并且这些修饰符在它前面
class Customer private /*@Inject*/ constructor(name: String)

//主构造函数不能包含任何的代码。初始化的代码可以放到以init关键字作为前缀的初始化块（initializer blocks）中。
//在实例初始化期间，初始化块按照它们出现在类体中的顺序执行，与属性初始化器交织在一起：
class Person(name: String) {
    val firstProperty = "First property: $name".also(::println)

    init {
        println("First initializer block that prints $name")
    }

    val secondProperty = "Second property: ${name.length}".also(::println)

    init {
        println("Second initializer block that prints ${name.length}")
    }

    var children:MutableList<Person> = mutableListOf()

    //如果类有主构造函数，每个次构造函数需要委托给主构造函数，可以直接委托或者通过别的次构造函数间接委托。委托到同一个类的另一个构造函数用this关键字即可
    constructor(parent: Person) : this(parent.firstProperty) {
        parent.children.add(this)
    }

    //初始化块(init{})中的代码实际上会成为主构造函数的一部分。委托给主构造函数会作为次构造函数的第一条语句，因此所有初始化块与属性初始化器中的代码都会在次构造函数体之前执行。
    //即使该类没有主构造函数，这种委托仍会隐式发生，并且仍会执行初始化块。
    constructor(name: String, parent: Person) : this(name) {
        parent.children.add(this)
    }
}

//如果一个非抽象类没有声明任何（主或次）构造函数，它会有一个生成的不带参数的主构造函数。构造函数的可见性是public，
//如果不希望类有一个公有构造函数，那么需要声明一个带有非默认可见性的空的主构造函数，如
class DontCreateMe private constructor()