package com.suheng.structure.view.kt.generic

class Everybody : Consumer<Food> {
    override fun consume(item: Food) {
        println("eat food")
    }
}

class ModernPeople : Consumer<FastFood> {
    override fun consume(item: FastFood) {
        println("eat fast food")
    }
}

class American : Consumer<Burger> {
    override fun consume(item: Burger) {
        println("eat burger")
    }
}