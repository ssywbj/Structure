package com.suheng.structure.view.kt

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

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
class Person(name: String) { //name在主构造方法没有用var或val声明，是私有属性（默认是private var），外部访问不到
    val firstProperty = "First property: $name".also(::println)

    init {
        println("First initializer block that prints $name")
    }

    val secondProperty = "Second property: ${name.length}".also(::println)

    init {
        println("Second initializer block that prints ${name.length}")
    }

    var children: MutableList<Person> = mutableListOf()

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

//在Kotlin中所有类都有一个共同的超类————Any，这对于没有超类型声明的类是默认超类。Any有三个方法：equals()、 hashCode()与toString()
class Example // 从Any隐式继承

//默认情况下，Kotlin类是最终（final）的：它们不能被继承。要使一个类可继承，请用open关键字标记它
open class Base(p: Int)
class Derived(j: Int) : Base(j) //如果派生类有一个主构造函数，其基类可以（并且必须）用派生类主构造函数的参数就地初始化。

class MyKtView : View { //如果派生类没有主构造函数，那么每个次构造函数必须使用super关键字初始化其基类型，或委托给另一个构造函数做到这一点
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    override fun onDraw(canvas: Canvas?) { //覆盖方法：override
        super.onDraw(canvas)
    }
}

open class Shape {
    open fun draw() {}
    fun fill() {} //如果函数没有标注open，如fill()，那么子类中不允许定义相同签名的函数，不论加不加override

    open val vertexCount: Int = 0
}

class Circle : Shape() {
    override fun draw() {
        super.draw()
        println("Circle draw")
    }

    //可以用var属性覆盖val属性，但反之则不行。 因为一个val属性本质上声明了一个get方法，而将其覆盖为var只是在子类中额外声明一个set方法
    override var/*val*/ vertexCount: Int = 2 //覆盖属性规则同覆盖方法
}

interface Shape2 {
    val vertexCount: Int
}

class Rectangle(override val vertexCount: Int = 4) : Shape2 //总是有4个顶点

class Polygon : Shape2 {
    override var vertexCount: Int = 0 //var，以后可以设置为任何数

    private fun testVertexCount() {
        vertexCount = 14
    }
}

open class Base2(val name: String) {
    init {
        println("Initializing Base2")
    }

    open val size: Int = name.length.also { println("Initializing size in Base2: $it") }

    open fun draw() {
        println("Base2, draw, draw")
    }
}

class Derived2(name: String, lastName: String) :
    Base2(
        name.replaceFirstChar { it.titlecase() } //首字母变大写
            .also { println("Argument for Base2: $it") }) { //also{...}：调用到这个属性的时候，顺便执行一下其它语句
    init {
        println("Initializing Derived2")
    }

    override val size: Int =
        (super.size + lastName.length).also { println("Initializing size in Derived2: $it") } //"super.size"：使用super关键字访问超类属性或方法

    override fun draw() {
        println("Derived2, draw, draw")
        Filler().drawAndFill()
    }

    inner class Filler { //内部类：inner
        fun fill() {
            println("Filling")
        }

        fun drawAndFill() {
            //draw() //调用Derived2的draw()实现，而Derived2的draw()又调用了Filler的drawAndFill()，因为会陷入死循环
            //在一个内部类中访问外部类的超类，可以通过由外部类名限定的super关键字来实现：super@Outer
            super@Derived2.draw() //调用Base2的draw()实现
            fill()
            println("Drawn a filled Base2 with size ${super@Derived2.size}") //使用Base2所实现的size
        }
    }
}
