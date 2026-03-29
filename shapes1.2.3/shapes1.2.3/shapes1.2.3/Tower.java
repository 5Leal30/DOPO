import java.util.ArrayList;
import java.util.Collections;
/**
 * Write a description of class Tower here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Tower
{
    private int SCALE = 20;
    private static final int margen = 30;
    private int heightTower;
    private int widthTower;
    private boolean isVisible;
    private boolean itsOk;
    private ArrayList<Cup> cups;
    private ArrayList<Lid> lids;
    private int currentHeight;
    private boolean lastOperationOk;
    
    
    /**
     * Crea una torre vacia con el ancho y alto maximo
     */
    public Tower(int width, int maxHeight)
    {
        this.widthTower = width * SCALE;
        this.SCALE = 20;
        this.heightTower = maxHeight;
        this.currentHeight = 0;
        this.isVisible = false;
        this.lastOperationOk = true;
        this.cups = new ArrayList<Cup>();
        this.lids = new ArrayList<Lid>();
    }
    
    /**
     * Crea una torre con tazas de la 1 a la numCups,
     * cada taza tiene una altura de 2*i-1
     */
    public Tower(int numCups){
        this.cups = new ArrayList<Cup>();
        this.lids = new ArrayList<Lid>();
        this.currentHeight = 0;
        this.isVisible = false;
        this.lastOperationOk = true;
        int totalHeight = 0;
        int maxWidth = 0;
        for (int i = 1; i <= numCups; i++){
            totalHeight += (2 * i - 1);
            if ((2*i-1) > maxWidth){
                maxWidth = (2 * i - 1);
            }
        }
        this.heightTower = totalHeight;
        this.SCALE = 500/ totalHeight;
        this.widthTower = maxWidth * SCALE;
        int centroTorre = margen + (widthTower / 2);
        int grosor = SCALE;
        int anchoMinimo = SCALE * 3;
        int yFondoBase = heightTower * SCALE;
        boolean primeraCup = true;
        for (int i = numCups; i >= 1; i--){
            int anchoTaza = (2 * i - 1) * SCALE;
            Cup cup = new Cup(i, anchoTaza, SCALE);
            int xCentrada = centroTorre - (anchoTaza / 2);
            int yParedes = yFondoBase - cup.getCupHeightPx();
            cup.setPosition(xCentrada, yParedes);
            cups.add(cup);
            yFondoBase = yFondoBase - grosor;
        }
        this.currentHeight = 2 * numCups - 1;
    }

    /**
     * Agrega una taza i en la parte superior de la torre, y 
     * no se agrega si ya existe o si no cabe.
     */
    public void pushCup(int i)
    {
        if (!existeCup(i) && cabe(i)){
            int anchoProporcional = (2 * i - 1) * SCALE;
            Cup cup = new Cup(i, anchoProporcional,  SCALE);
            int yPosition = calcYPosition(cup);
            if(yPosition != -1){
                int yCenter = margen + (widthTower/2);
                int xCenter = yCenter - (cup.getCupWidth() / 2);
                cup.setPosition(xCenter, yCenter);
                cups.add(cup);
            }
            if(isVisible){
                cup.makeVisible();
            }
            currentHeight += cup.getCupHeight();
            orderCupsPosition();
        }
    }
    
    /**
     * Va a calcular la posición en la que debe quedar la copa i
     */
    public int calcYPosition(Cup cup){
        if(cups.isEmpty() && lids.isEmpty()){
            return heightTower * SCALE - cup.getCupHeightPx();
        }
        int newCupSize = cup.getCupNumber();
        
        for(int i = cups.size() - 1; i >= 0; i--){
            Cup actualCup = cups.get(i);
            int actualSize = actualCup.getCupNumber();
            int yPositionNewCup = actualCup.getYPosition();
            int xPositionNewCup = actualCup.getXPosition();
            boolean hasLid = actualCup.hasLid();
            if(hasLid){
                return yPositionNewCup - cup.getCupHeightPx();
            }else if(newCupSize < actualSize){
                boolean isCupInside = isCupInside(cup);
                if(!isCupInside){
                    return yPositionNewCup + actualCup.getCupHeightPx() - cup.getCupHeightPx();
                }
            }else if(newCupSize > actualSize){
              return yPositionNewCup - cup.getCupHeightPx();
            }
        }
        
        if(!cups.isEmpty()){
            Cup upperCup = cups.get(cups.size()-1);
            return upperCup.getYPosition() - cup.getCupHeightPx();
        }
        
        return heightTower * SCALE - cup.getCupHeightPx();
    }
    
    /**
     * Ordena la torre de acuerdo a la información guardada
     */
    public void orderCupsPosition(){
        for(int a = 0; a < cups.size() - 1; a++){
            cups.get(a).makeInvisible();
            for(int b = a + 1; b < cups.size(); b++){
                if(cups.get(a).getYPosition() > cups.get(b).getYPosition()){
                    Cup temp = cups.get(a);
                    cups.set(a,cups.get(b));
                    cups.set(b,temp);
                }
            }
            cups.get(a).makeVisible();
        } 
    }
    
    /**
     * Mira si hay copas adentro de otra copa
     */
    private boolean isCupInside(Cup bigCup){
        int yBigCup = bigCup.getYPosition();
        int bottomBigCup = yBigCup + bigCup.getCupHeightPx();
        for(Cup posibleLittleCup : cups){
            int yLittleCup = posibleLittleCup.getYPosition();
            if(yLittleCup < yBigCup && (yLittleCup + posibleLittleCup.getCupHeightPx()) >= bottomBigCup){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Elimina la taza que está en la parte superior
     */
    public void popCup()
    {
        if (!cups.isEmpty() && ultimoElementoEsTaza()){
            Cup top = cups.get(cups.size()-1);
            currentHeight -= top.getCupHeight();
            top.makeInvisible();
            cups.remove(cups.size()-1);
            lastOperationOk = true;
        }else{
            lastOperationOk = false;
        }
        
    }
    
    /**
     * Elimina la taza i de cualquier posición, y si tiene tapa
     * también se elimina la taza
     */
    public void removeCup(int i){
        if (existeCup(i)){
            Cup cup = buscarCup(i);
            if (cup.hasLid()){
                cup.getLid().setCup(null);
                cup.setLid(null);
            }
            currentHeight -= cup.getCupHeight();
            cup.makeInvisible();
            cups.remove(cup);
            redibujarElementos();
            lastOperationOk = true;
        }else{
            lastOperationOk = false;
        }
    }
    
    /**
     * Añade una tapa i encima de la torre, si existe una taza i
     * se asocia y no se agrega si ya existe o no cabe
     */
    public void pushLid(int i){
        if (!existeLid(i) && currentHeight + 1 <= heightTower){
            int anchoTapa = (2 * i - 1) * SCALE;
            int centroTorre = margen + (widthTower / 2);
            int xCentrada = centroTorre - (anchoTapa / 2);
            int yPixels = (heightTower - currentHeight - 1)* SCALE;
            if (existeCup(i)){
                anchoTapa = buscarCup(i).getCupWidth();
            }else{
                anchoTapa = widthTower;
            }
            Lid lid = new Lid(i, anchoTapa);
            lid.setPosition(xCentrada, yPixels);
            if (isVisible){
                lid.makeVisible();
            }
            lids.add(lid);
            if (existeCup(i)){
                Cup cup =buscarCup(i);
                cup.setLid(lid);
                lid.setCup(cup);
            }
            currentHeight += 1;
            lastOperationOk = true;
        }else{
            lastOperationOk = false;
        }
    }
    
    /**
     * Elimina la tapa que está en la parte superior de la torre
     */
    public void popLid(){
        if (!lids.isEmpty() && ultimoElementoEsTapa()){
            Lid top = lids.get(lids.size()-1);
            if (top.getCup() != null){
                top.getCup().setLid(null);
                top.setCup(null);
            }
            currentHeight -= 1;
            top.makeInvisible();
            lids.remove(lids.size() - 1);
            lastOperationOk = true;
        }else{
            lastOperationOk = false;
        }
    }
    
    /**
     * Elimina la tapa i de cualquier posición, si tiene una taza
     * asociada, se elimina la asociación, no la taza.
     */
    public void removeLid(int i)
    {
     if (existeLid(i)){
         Lid lid = buscarLid(i);
         if (lid.getCup() != null){
             lid.getCup().setLid(null);
             lid.setCup(null);
         }
         currentHeight -= 1;
         lid.makeInvisible();
         lids.remove(lid);
         redibujarElementos();
         lastOperationOk = true;
     }else{
         lastOperationOk = false;
     }
        
    }
    
    
    /**
     * Ordena los elementos de la torre de mayor a menor,
     * de abajo para arriba, tambien mira si caben los elementos y
     * los pares copas y tapas se mueven juntos
     */
    public void orderTower()
    {
        ArrayList<Object[]> elementos = recolectarElementos();
        ordenarElementos(elementos, false);
        ArrayList<Object[]> queCaben = calcularQueCaben(elementos);
        redibujarConUnidades(queCaben);
        lastOperationOk = true;
    }
    
    /**
     * rdena los elementos de la torre de menor a mayor, de abajo 
     * para arriba, tambien maneja los pares y los que quepan
     */
    public void reverseTower()
    {
        ArrayList<Object[]> units = recolectarElementos();
        ordenarElementos(units, true);
        ArrayList<Object[]> queCaben = calcularQueCaben(units);
        redibujarConUnidades(queCaben);
        lastOperationOk = true;
    }
    
    /**
     * Intercambia la posición de dos objetos de la torre, 
     * si una taza tiene tiene tapa, se mueven juntas
     */
    public void swap(String[] o1, String[] o2){
        if (existeObjeto(o1) && existeObjeto(o2)){
            intercambiarPosiciones(o1, o2);
            lastOperationOk = true;
        }else{
            lastOperationOk = false;
        }
    }
    
    /**
     * Tapa todas las tazas que tienen tapa pero que aún no estan
     * asociadas y luego reorganiza.
     */
    public void cover(){
        for (Cup cup : cups){
            if (!cup.hasLid() && existeLid(cup.getCupNumber())){
                Lid lid = buscarLid(cup.getCupNumber());
                cup.setLid(lid);
                lid.setCup(cup);
            }
        }
        redibujarElementos();
        lastOperationOk = true;
    }
    
    /**
     * Retorna altura actual de la torre
     */
    public int height()
    {
       return currentHeight; 
    }
    
    /**
     * Retorna un arreglo con los numeros de las tazas tapadas, 
     * de menor a mayor
     */
    public int[] lidedCups()
    {
       ArrayList<Integer> result = new ArrayList<Integer>();
       for(Cup cup : cups){
           if (cup.hasLid()){
               result.add(cup.getCupNumber());
           }
       }
       Collections.sort(result);
       int[] arr = new int[result.size()];
       for (int i = 0; i < result.size(); i++){
               arr[i] = result.get(i);
       }
       return arr;
    }
    
    /**
     * Retorna un arreglo de a pares, (osea {"cup/lid","num"}),
     * con los elementos de abajo para arriba
     */
    public String[][] stackingItems()
    {
        ArrayList<String[]> items = new ArrayList<String[]>();
        for (Cup cup : cups){
            items.add(new String[]{"cup", String.valueOf(cup.getCupNumber())});
            if (cup.hasLid()){
                items.add(new String[]{"lid", String.valueOf(cup.getLid().getLidNumber())});
            }
        }
        return items.toArray(new String[0][]);
    }  
    
    /**
     * Retorna un par de objetos que si se llegan a intercambiar
     * se reduce la altura de la torre, si no se puede retorna null
     */
    public  String[][] swapToReduce(){
        ArrayList<Object[]> units = recolectarElementos();
        int alturaActual = calcularAlturaElementos(units);
        for (int i = 0; i < units.size() - 1; i++){
            for (int j = i + 1; j < units.size(); j++){
                if (simularIntercambio(units, i,j) < alturaActual){
                    Cup c1 = (Cup) units.get(i)[0];
                    Cup c2 = (Cup) units.get(j)[0];
                    return new String[][]{
                        {"cup", String.valueOf(c1.getCupNumber())},
                        {"cup", String.valueOf(c2.getCupNumber())}
                    };
                }
            }
        }
        return null;
    }
    
    /**
     * Hace visible todos los elementos
     */
    public void makeVisible()
    {
       isVisible = true;
       Canvas.getCanvas().setVisible(true);
       for (Cup cup : cups){
           cup.makeVisible();
       }
       
       for (Lid lid: lids){
           lid.makeVisible();
       }
    }
    
    /**
     * Hace invisiles todos los elementos
     */
    public void makeInvisible()
    {
        isVisible = false;
        Canvas.getCanvas().setVisible(false);
        for (Cup cup: cups){
            cup.makeInvisible();
        }
        
        for (Lid lid: lids){
            lid.makeInvisible();
        }
    }

    /**
     * Termina el proceso, vuelve todo invisible
     */
    public void exit()
    {
        makeInvisible();
    }
    
    /**
     * Revisa si la ultima operación fue correcta
     */
    public boolean ok()
    {
        return lastOperationOk;
    }
    
    /**
     * Mira si existe una taza i en la torre
     */
    private boolean existeCup(int i){
        return buscarCup(i) != null;
    }
    
    /**
     * Mira si existe una tapa i en la torre
     */
    private boolean existeLid(int i){
        return buscarLid(i) != null;
    }
    
    /**
     * Mira si existe un objeto identificado por tipo y numero
     */
    private boolean existeObjeto(String[] o){
        int num = Integer.parseInt(o[1]);
        if (o[0].equals("cup")){
            return existeCup(num);
        }
        
        if(o[0].equals("lid")){
            return existeLid(num);
        }
        return false;
    }
    
    /**
     * Revisa si el elemento de la cima es una tapa
     */
    private boolean ultimoElementoEsTapa(){
        if (lids.isEmpty()){
            return false;
        }
        
        if (cups.isEmpty()){
            return true;
        }
        Cup topCup = cups.get(cups.size() - 1);
        Lid topLid = lids.get(lids.size() - 1);
        if (topLid.getLidNumber() == topCup.getCupNumber()){
            return true;
        }
        
        if (lids.size() > cups.size()){
            return true;
        }
        return false;
    }
    
    /**
     * Revisa si el ultimo elemento es una taza
     */
    private boolean ultimoElementoEsTaza(){
        if (cups.isEmpty()){
            return false;
        }
        
        if (lids.isEmpty()){
            return true;
        }
        Cup topCup = cups.get(cups.size() - 1);
        Lid topLid = lids.get(lids.size() - 1);
        if (topCup.getCupNumber() != topLid.getLidNumber()){
            return true;
        }
        return false;
        
    }
    
    /**
     * Mira si una taza i cabe en la torre
     */
    private boolean cabe(int i){
        return currentHeight + i <= heightTower;
    }
    
    /**
     * Busca si la copa i, existe
     */
    private Cup buscarCup(int i){
        for (Cup cup:cups){
            if (cup.getCupNumber() == i){
                return cup;
            }
        }
        return null;
    }
    
    /**
     * Busca si la tapa i, existe
     */
    private Lid buscarLid(int i){
        for (Lid lid : lids){
            if (lid.getLidNumber() == i){
                return lid;
            }
        }
        return null;
    }
    
    /**
     * Hace invisibles los elementos y los redibuja en sus posiciones
     * correctas
     */
    private void redibujarElementos(){
        for (Cup cup : cups){
            cup.makeInvisible();
        }
    
        for (Lid lid : lids){
            lid.makeInvisible();
        }
    
        for (Cup cup: cups){
            if (isVisible){
                cup.makeVisible();
            }
        

            if (cup.hasLid()){
                int xTapa = cup.getXPosition();
                int yTapa = cup.getYPosition();
                cup.getLid().setPosition(xTapa, yTapa);
                if (isVisible){
                    cup.getLid().makeVisible();
                }
            }
        }
    
        for (Lid lid: lids){
            if (lid.getCup() == null){
                if (isVisible){
                    lid.makeVisible();
                }
            }
        }
    }
    
    /**
     * Recolecta todos los elementos de la torre
     */
    private ArrayList<Object[]> recolectarElementos(){
        ArrayList<Object[]> units = new ArrayList<Object[]>();
        for (Cup cup : cups){
            units.add(new Object[]{cup, cup.getLid()});
        }
        
        for (Lid lid : lids){
            if (lid.getCup() == null){
                units.add(new Object[]{null, lid});
            }
        }
        return units;
    }
    
    /**
     * Ordena la lista de elementos de mayor a menor si ascendente
     * es falso, en caso contrario lo organiza de menor a mayor
     */
    private void ordenarElementos(ArrayList<Object[]> units, boolean ascendente){
        for (int i = 0; i < units.size() - 1; i++){
            for (int j = i + 1; j < units.size(); j++){
                int a = getUnitNumber(units.get(i));
                int b = getUnitNumber(units.get(j));
                boolean swap;
                if (ascendente){
                    swap = a > b;
                }else{
                    swap = a < b;
                }
                
                if (swap){
                    Object[] temp = units.get(i);
                    units.set(i, units.get(j));
                    units.set(j, temp);
                }
            }
        }
    }
    
    /**
     * Retorna el identificador de una unidad
     */
    private int getUnitNumber(Object[] unit){
        if(unit[0] != null){
            return ((Cup) unit[0]).getCupNumber();
        }
        return ((Lid) unit[1]).getLidNumber();
    }
    
    /**
     * Retorna la altura de una unidad
     */
    private int getUnitHeight(Object[] unit){
        int h = 0;
        if (unit[0] != null){
            h += ((Cup) unit[0]).getCupHeight();
        }
        
        if(unit[1] != null){
            h += 1;
        }
        return h;
    }
    
    /**
     * Retorna los elementos que caben dentro de la altura maxima
     * de la torre
     */
    private ArrayList<Object[]> calcularQueCaben(ArrayList<Object[]> units){
        ArrayList<Object[]> result = new ArrayList<Object[]>();
        int total = 0;
        for (Object[] unit : units){
            int h = getUnitHeight(unit);
            if (total + h <= heightTower){
                result.add(unit);
                total += h;
            }
        }
        return result;
    }
    
    /**
     * Limpia la torre y la redibuja usando la lista de elementos,
     * actualiza: cups, lids y currentHeight
     */
    private void redibujarConUnidades(ArrayList<Object[]> units){
        for (Cup cup : cups){
            cup.makeInvisible();
        }
    
        for (Lid lid: lids){
            lid.makeInvisible();
        }
        cups.clear();
        lids.clear();
        currentHeight = 0;
    
        for (int a = 0; a < units.size() - 1; a++){
            for (int b = a + 1; b < units.size(); b++){
                if (getUnitNumber(units.get(a)) < getUnitNumber(units.get(b))){
                    Object[] temp = units.get(a);
                    units.set(a, units.get(b));
                    units.set(b, temp);
                }
            }
        }
    
        int centroTorre = margen + (widthTower / 2);
        int yFondo = heightTower * SCALE;
        System.out.println("heightTower="+heightTower+" SCALE="+SCALE+" yFondo="+yFondo);
        for (Object[] unit : units){
            if (unit[0] != null){
                Cup cup = (Cup) unit[0];
                System.out.println("cup="+cup.getCupNumber()+" cupHeightPx="+cup.getCupHeightPx());
                int xCentrada = centroTorre - (cup.getCupWidth() / 2);
                int yParedes = yFondo - cup.getCupHeightPx();
                cup.setPosition(xCentrada, yParedes);
                if (isVisible){
                    cup.makeVisible();
                }
                cups.add(cup);
                currentHeight += cup.getCupHeight();
                yFondo = yParedes + SCALE;
            }
        
            if (unit[1] != null){
                Lid lid = (Lid) unit[1];
                int xCentrada = centroTorre - (lid.getLidWidth() / 2);
                lid.setPosition(xCentrada, yFondo);
                if (isVisible){
                    lid.makeVisible();
                }
                lids.add(lid);
                currentHeight += 1;
                yFondo = yFondo - SCALE;
            }
        }
    }
    
    /**
     * Intecrambia posiciones entre dos elementos
     */
    private void intercambiarPosiciones(String[] o1, String [] o2){
        ArrayList<Object[]> units = recolectarElementos();
        int index1 = -1;
        int index2 = -1;
        for (int i = 0; i < units.size(); i++){
            int num = getUnitNumber(units.get(i));
            String tipo;
            if (units.get(i)[0] != null){
                tipo = "cup";
            }else{
                tipo = "lid";
            }
            
            if (tipo.equals(o1[0]) && num == Integer.parseInt(o1[1])){
                index1 = i;  
            }
            
            if (tipo.equals(o2[0]) && num == Integer.parseInt(o2[1])){
                index2 = i;
            }
        }

            
            
        if(index1 >= 0 && index2 >= 0){
            Object[] temp = units.get(index1);
            units.set(index1, units.get(index2));
            units.set(index2, temp);
            redibujarConUnidades(units);
        }
        
    }
    
    /**
     * Retorna la altura total de una lista de unidades
     */
    private int calcularAlturaElementos(ArrayList<Object[]> units){
        int total = 0;
        for (Object[] unit : units){
            total += getUnitHeight(unit);
        }
        return total;
    }
    
    /**
     * Simula un intecrambio para ver su es posible modificarlo o
     * no, al ser una simulación no modifica la original
     */
    private int simularIntercambio(ArrayList<Object[]> units, int i, int j){
        ArrayList<Object[]> copia = new ArrayList<Object[]>(units);
        Object[] temp = copia.get(i);
        copia.set(i, copia.get(j));
        copia.set(j, temp);
        return calcularAlturaElementos(copia);
    }
    
}
