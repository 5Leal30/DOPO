import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TowerC2Test
{
    private Tower tower;

    public TowerC2Test(){}

    @BeforeEach
    public void setUp(){
        tower = new Tower(10, 20);
    }

    @AfterEach
    public void tearDown(){
        tower = null;
    }

    @Test
    public void accordingLShould_pushCupAgregaTaza(){
        tower.pushCup(3);
        assertTrue(tower.ok());
        assertEquals(3, tower.height());
    }

    @Test
    public void accordingLShould_pushCupVariasTazas(){
        tower.pushCup(3);
        tower.pushCup(1);
        assertTrue(tower.ok());
        assertEquals(4, tower.height());
    }

    @Test
    public void accordingLShouldNot_pushCupDuplicada(){
        tower.pushCup(3);
        tower.pushCup(3);
        assertFalse(tower.ok());
    }

    @Test
    public void accordingLShouldNot_pushCupNoCabe(){
        tower.pushCup(5);
        tower.pushCup(4);
        tower.pushCup(3);
        tower.pushCup(2);
        tower.pushCup(1); 
        tower.pushCup(6); 
        assertFalse(tower.ok());
        assertEquals(15, tower.height());
    }

    @Test
    public void accordingLShould_popCupQuitaUltimaTaza(){
        tower.pushCup(3);
        tower.pushCup(1);
        tower.popCup();
        assertTrue(tower.ok());
        assertEquals(3, tower.height());
    }

    @Test
    public void accordingLShouldNot_popCupTorreVacia(){
        tower.popCup();
        assertFalse(tower.ok());
    }

    @Test
    public void accordingLShouldNot_popCupCimaTapa(){
        tower.pushCup(3);
        tower.pushLid(3);
        tower.popCup();
        assertFalse(tower.ok());
    }

    @Test
    public void accordingLShould_removeCupExistente(){
        tower.pushCup(3);
        tower.pushCup(1);
        tower.removeCup(3);
        assertTrue(tower.ok());
        assertEquals(1, tower.height());
    }

    @Test
    public void accordingLShouldNot_removeCupNoExiste(){
        tower.removeCup(5);
        assertFalse(tower.ok());
    }

    @Test
    public void accordingLShould_pushLidAgregaTapa(){
        tower.pushCup(3);
        tower.pushLid(3);
        assertTrue(tower.ok());
        assertEquals(4, tower.height());
    }

    @Test
    public void accordingLShould_pushLidAsociaCup(){
        tower.pushCup(3);
        tower.pushLid(3);
        assertEquals(1, tower.lidedCups().length);
        assertEquals(3, tower.lidedCups()[0]);
    }

    @Test
    public void accordingLShouldNot_pushLidDuplicada(){
        tower.pushCup(3);
        tower.pushLid(3);
        tower.pushLid(3);
        assertFalse(tower.ok());
    }

    @Test
    public void accordingLShould_orderTowerOrdenaMayorAMenor(){
        tower.pushCup(1);
        tower.pushCup(3);
        tower.pushCup(2);
        tower.orderTower();
        assertTrue(tower.ok());
        String[][] items = tower.stackingItems();
        assertEquals("3", items[0][1]);
        assertEquals("2", items[1][1]);
        assertEquals("1", items[2][1]);
    }

    @Test
    public void accordingLShould_reverseTowerOrdenaMenorAMayor(){
        tower.pushCup(3);
        tower.pushCup(1);
        tower.pushCup(2);
        tower.reverseTower();
        assertTrue(tower.ok());
        String[][] items = tower.stackingItems();
        assertEquals("1", items[0][1]);
        assertEquals("2", items[1][1]);
        assertEquals("3", items[2][1]);
    }

    @Test
    public void accordingLShould_lidedCupsRetornaOrdenado(){
        tower.pushCup(3);
        tower.pushCup(1);
        tower.pushLid(3);
        tower.pushLid(1);
        int[] lided = tower.lidedCups();
        assertEquals(2, lided.length);
        assertEquals(1, lided[0]);
        assertEquals(3, lided[1]);
    }

    @Test
    public void accordingLShould_lidedCupsSinTapas(){
        tower.pushCup(3);
        assertEquals(0, tower.lidedCups().length);
    }


    @Test
    public void accordingLShould_constructorNumCupsCreaCorrectamente(){
        Tower t2 = new Tower(4);
        assertEquals(16, t2.height()); // 1+3+5+7=16
        assertTrue(t2.ok());
    }

    @Test
    public void accordingLShould_swapIntercambiaElementos(){
        tower.pushCup(3);
        tower.pushCup(1);
        String[] o1 = {"cup", "3"};
        String[] o2 = {"cup", "1"};
        tower.swap(o1, o2);
        assertTrue(tower.ok());
    }


    @Test
    public void accordingLShouldNot_swapElementoNoExiste(){
        tower.pushCup(3);
        String[] o1 = {"cup", "3"};
        String[] o2 = {"cup", "9"};
        tower.swap(o1, o2);
        assertFalse(tower.ok());
    }

    @Test
    public void accordingLShould_coverTapaTazas(){
        tower.pushCup(3);
        tower.pushCup(1);
        tower.pushLid(3);
        tower.cover();
        assertTrue(tower.ok());
        assertEquals(1, tower.lidedCups().length);
    }

    @Test
    public void accordingLShould_swapToReduceRetornaPar(){
        Tower t2 = new Tower(4);
        String[][] result = t2.swapToReduce();
        assertNotNull(result);
        assertEquals(2, result.length);
    }

    @Test
    public void accordingLShould_swapToReduceRetornaNullSiNoReduce(){
        tower.pushCup(1);
        assertNull(tower.swapToReduce());
    }
}