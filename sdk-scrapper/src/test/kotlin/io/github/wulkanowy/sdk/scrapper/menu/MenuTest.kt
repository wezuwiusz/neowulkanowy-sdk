package io.github.wulkanowy.sdk.scrapper.menu

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class MenuTest : BaseLocalTest() {

    private val menu by lazy {
        runBlocking {
            getStudentRepo(MenuTest::class.java, "Jadlospis.json")
                .getMenu(LocalDate.of(2023, 4, 28))
        }
    }

    @Test
    fun `contains basic menu information`() {
        assertEquals(1, menu.size)
        with(menu[0]) {
            assertEquals(getDate(2023, 4, 28), date)
            assertEquals("młodzież", diet)
            assertEquals(1, id)
            assertEquals(3, menu[0].meals.size)
        }
    }

    @Test
    fun `contains information about meal's ingredients`() {
        assert(menu.isNotEmpty())
        assert(menu[0].meals.isNotEmpty())
        val meal = menu[0].meals[0]
        assertEquals(13, meal.ingredients.size)
        with(meal.ingredients[11]) {
            assertEquals(1, counter)
            assertEquals("jogurt owocowy", recipe)
            assertEquals("gram", measurementUnit)
            assertEquals(150, measurementUnitValue)
        }
    }

    @Test
    fun `contains information about meal's allergens`() {
        assert(menu.isNotEmpty())
        assert(menu[0].meals.isNotEmpty())
        val meal = menu[0].meals[0]
        assertEquals(10, meal.allergens.size)
        assertEquals("Orzechy", meal.allergens[5])
    }

    @Test
    fun `contains meal's details`() {
        assert(menu.isNotEmpty())
        assert(menu[0].meals.isNotEmpty())
        val meal = menu[0].meals[0]
        assertEquals(22, meal.details.size)
        with(meal.details[0]) {
            assertEquals("Wartość energetyczna", label)
            assertEquals("1140 kcal", value)
        }
    }
}
