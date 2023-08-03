package com.suheng.structure.view.kt.generic

class FoodStore : Production<Food> {
    override fun produce(): Food {
        println("produce food")
        return Food()
    }
}

class FastFoodStore : Production<FastFood> {
    override fun produce(): FastFood {
        println("produce fast food")
        return FastFood()
    }
}

class BurgerStore : Production<Burger> {
    override fun produce(): Burger {
        println("produce burger")
        return Burger()
    }
}
