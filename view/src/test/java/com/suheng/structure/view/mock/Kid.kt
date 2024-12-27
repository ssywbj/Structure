package com.suheng.structure.view.mock

class Kid(private val mother: Mother) {

    var money = 0

    fun wantMoney() {
        money += mother.giveMoney()
        mother.doSomeThing()
    }

}