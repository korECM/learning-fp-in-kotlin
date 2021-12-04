package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class Chap8 : StringSpec({
    "Example 1" {
        val listOfPartiallyAppliedFunctions: Functor<(Int) -> Int> =
            funListOf(1, 2, 3, 4).fmap { x: Int -> { y: Int -> x * y } }

        listOfPartiallyAppliedFunctions.fmap { it -> it(5) }.shouldBe(funListOf(5, 10, 15, 20))
        listOfPartiallyAppliedFunctions.fmap { it -> it(0) }.shouldBe(funListOf(0, 0, 0, 0))
        listOfPartiallyAppliedFunctions.fmap { it -> it(10) }.shouldBe(funListOf(10, 20, 30, 40))
    }

    "Example 2 and 3" {
        funListOf(1, 2).append(funListOf(3, 4)) shouldBe funListOf(1, 2, 3, 4)

        funListOf<(Int) -> Int>({ it * it }) apply funListOf(1, 2, 3) shouldBe funListOf(1, 4, 9)
        funListOf<(Int) -> Int>({ it * it }, { it * 10 }).apply(funListOf(1, 2, 3))
            .shouldBe(funListOf(1, 4, 9, 10, 20, 30))

        FunList.pure(10) shouldBe funListOf(10)
    }

    "Example 4" {
        val tree = Node(1, listOf(Node(2), Node(3)))
        tree.fmap { it * 2 }.toString() shouldBe "2 [4 [], 6 []]"

        (Tree.pure { x: Int -> x * 2 } apply tree).toString() shouldBe "2 [4 [], 6 []]"

        (
            Tree.pure({ x: Int, y: Int -> x * y }.curried())
                apply Node(1, listOf(Node(2), Node(3)))
                apply Node(4, listOf(Node(5), Node(6)))
            ).toString() shouldBe "4 [5 [], 6 [], 8 [10 [], 12 []], 12 [15 [], 18 []]]"

        (
            Tree.pure({ x: Int, y: Int -> x * y }.curried())
                apply Node(4, listOf(Node(5), Node(6)))
                apply Node(1, listOf(Node(2), Node(3)))
            ).toString() shouldBe "4 [8 [], 12 [], 5 [10 [], 15 []], 6 [12 [], 18 []]]"
    }

    "Example 5" {
        (
            Tree.pure({ x: Int, y: Int -> x * y }.curried())
                apply Node(1, listOf(Node(2, listOf(Node(3))), Node(4)))
                apply Node(5, listOf(Node(6), Node(7, listOf(Node(8), Node(9)))))
            ).toString() shouldBe "5 [6 [], 7 [8 [], 9 []], 10 [12 [], 14 [16 [], 18 []], 15 [18 [], 21 [24 [], 27 []]]], 20 [24 [], 28 [32 [], 36 []]]]"
    }

    "Example 6" {
        (funListOf<(Int) -> Int>({ it * 5 }, { it + 10 }) myZip funListOf(10, 20, 30)).toString("") shouldBe "[50, 30]"
        funListOf<(Int) -> Int>({ it * 5 }, { it + 10 }).myZipTailrec(funListOf(10, 20, 30))
            .toString("") shouldBe "[50, 30]"
    }

    "EitherTest" {
        Right(10).fmap { it * 2 }.toString() shouldBe "Right(20)"
        Left("error").fmap { x: String -> "$x log" }.toString() shouldBe "Left(error)"

        Either.pure(10).toString() shouldBe "Right(10)"
        Either.pure { x: Int -> x * 2 }.toString() shouldBe "Right((kotlin.Int) -> kotlin.Int)"

        (Either.pure { x: Int -> x * 2 } apply Left("error")).toString() shouldBe "Left(error)"
        (Either.pure { x: Int -> x * 2 } apply Right(10)).toString() shouldBe "Right(20)"

        (
            Either.pure({ x: Int, y: Int -> x * y }.curried())
                apply Left("error")
                apply Right(10)
            ).toString() shouldBe "Left(error)"
        (
            Either.pure({ x: Int, y: Int -> x * y }.curried())
                apply Right(10)
                apply Right(20)
            ).toString() shouldBe "Right(200)"
    }

    "Example 7" {
        val identity: (Int) -> Int = { it }
        val list = funListOf(1)
        (FunList.pure(identity) apply list) shouldBe list
    }

    "Example 8" {
        val expected = funListOf(169)

        val af1 = funListOf<(Int) -> Int>({ it * it })
        val af2 = funListOf<(Int) -> Int>({ it + 10 })
        val af3 = funListOf(3)

        (FunList.pure(compose<Int, Int, Int>().curried()) apply af1 apply af2 apply af3) shouldBe expected
        (af1 apply (af2 apply af3)).shouldBe(expected)
    }
})
