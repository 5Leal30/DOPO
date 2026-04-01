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
            Cup cup = new Cup(i, anchoProporcional, SCALE);
            int yPosition = calcYPosition(cup);
            if(yPosition != -1){
                int centerTower = margen + (widthTower / 2);
                int xCentered = centerTower - (cup.getCupWidth() / 2);
            
                if(!cups.isEmpty()){
                    Cup cupToPlaceOn = findCupBelow(yPosition, cup);
                
                    if(cupToPlaceOn != null){
                        int xInside = cupToPlaceOn.getXPosition() + (cupToPlaceOn.getCupWidth() - cup.getCupWidth()) / 2;
                        cup.setPosition(xInside, yPosition);
                    } else {
                        cup.setPosition(xCentered, yPosition);
                    }
                } else {
                    cup.setPosition(xCentered, yPosition);
                }
                cups.add(cup);
            
                if(isVisible){
                    cup.makeVisible();
                }
                currentHeight += cup.getCupHeight();
                orderCupsPosition();
                lastOperationOk = true;
            } else {
                lastOperationOk = false;
            }
        } else {
            lastOperationOk = false;
        }
    }
    
    /**
    * Va a calcular la posición en la que debe quedar la copa i
    * viendo si ademas de tazas, tambien hay tapas
    */
    public int calcYPosition(Cup cup){
        if(cups.isEmpty() && lids.isEmpty()){
            return heightTower * SCALE - cup.getCupHeightPx();
        }
        int newCupSize = cup.getCupNumber();
        Cup bestContainer = null;
        Cup cupBelow = null;
        for(Cup currentCup : cups){
            if(isCupAccessible(currentCup)){
                if(cup.getCupNumber() < currentCup.getCupNumber()){
                    Cup innerCup = getCupDirectlyBelow(currentCup);
            
                    if(innerCup == null){
                        if(bestContainer == null || currentCup.getCupNumber() < bestContainer.getCupNumber()){
                            bestContainer = currentCup;
                            cupBelow = null;
                        }
                    } else {
                        if(cup.getCupNumber() < innerCup.getCupNumber()){
                            Cup deepestContainer = findProperContainer(innerCup, cup);
                            if(deepestContainer != null && isCupAccessible(deepestContainer)){
                                bestContainer = deepestContainer;
                                cupBelow = getCupDirectlyBelow(deepestContainer);
                            }
                        } else {
                            bestContainer = currentCup;
                            cupBelow = innerCup;
                        }
                    }
                }
            }
        }
        if(bestContainer != null){
            int nivelSuelo;
            if(cupBelow != null){
                nivelSuelo = cupBelow.getYPosition();
            } else {
                nivelSuelo = bestContainer.getYPosition() + bestContainer.getCupHeightPx() - 20;
            }
            int cimaActual = nivelSuelo;
            for (Lid l : lids) {
                if (l.getYPosition() >= bestContainer.getYPosition() && 
                    l.getYPosition() <= bestContainer.getYPosition() + bestContainer.getCupHeightPx()) {
                    
                    if (l.getYPosition() < cimaActual) {
                        cimaActual = l.getYPosition();
                    }
                }
            }
            return cimaActual - cup.getCupHeightPx();
        }
        if(!cups.isEmpty() || !lids.isEmpty()){
            int minY = heightTower * SCALE;
            for (Cup c : cups) if (c.getYPosition() < minY) minY = c.getYPosition();
            for (Lid l : lids) if (l.getYPosition() < minY) minY = l.getYPosition();
            
            return minY - cup.getCupHeightPx();
        }
        return heightTower * SCALE - cup.getCupHeightPx();
    }
    
    /**
    * Revisa si se puede entrar a la Copa
    */
    private boolean isCupAccessible(Cup targetCup){
        int yTarget = targetCup.getYPosition();
        int topTarget = yTarget;
        int bottomTarget = yTarget + targetCup.getCupHeightPx();
    
        for(Cup otherCup : cups){
            if(otherCup == targetCup) continue;
        
            int yOther = otherCup.getYPosition();
            int bottomOther = yOther + otherCup.getCupHeightPx();
        
            if(bottomOther == topTarget){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Obtiene la copa de mas arriba de la torre
     */
        private Cup getTopCup(){
        if(cups.isEmpty()){
            return null;
        }
        Cup topCup = cups.get(0);
        for(Cup cup : cups){
            if(cup.getYPosition() < topCup.getYPosition()){
                topCup = cup;
            }
        }
        return topCup;
    } 
    
    /**
     * Obtiene la copa mas alta dentro de la copa i
     */
    private Cup getCupDirectlyBelow(Cup parentCup){
        int yParent = parentCup.getYPosition();
        int bottomParent = yParent + parentCup.getCupHeightPx();
        Cup highestInner = null;
        int highestY = -1;
    
        for(Cup possibleInner : cups){
            if(possibleInner == parentCup) continue;
        
            int yInner = possibleInner.getYPosition();
            int bottomInner = yInner + possibleInner.getCupHeightPx();
        
            if(yInner >= yParent && bottomInner <= bottomParent){
                if(yInner > highestY){
                    highestY = yInner;
                    highestInner = possibleInner;
                }
            }
        }
        return highestInner;
    }
    
    /** 
     * Encuentra la copa apropiado para la copa
     */
    private Cup findProperContainer(Cup currentContainer, Cup newCup){
        Cup innerCup = getCupDirectlyBelow(currentContainer);
    
        if(innerCup == null){
            return currentContainer;
        } else if(newCup.getCupNumber() < innerCup.getCupNumber()){
            return findProperContainer(innerCup, newCup);
        } else {
            return currentContainer;
        }
    }

    /**
     * Finds the cup on which the new cup will be placed
     */
    private Cup findCupBelow(int yPosition, Cup newCup){
        for(Cup existingCup : cups){
            int bottomOfExisting = existingCup.getYPosition() + existingCup.getCupHeightPx();
            if(yPosition + newCup.getCupHeightPx() == bottomOfExisting){
                return existingCup;
            }
        }   
        return null;
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
            if(isVisible){
                for(Cup c : cups){
                    c.makeVisible();
                }
            }
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
            Cup top = cups.get(0);
            currentHeight -= top.getCupHeight();
            top.makeInvisible();
            cups.remove(top);
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
            ArrayList<Cup> cups2 = new ArrayList<>();
            for(Cup c : cups){
                cups2.add(c);
            }
            currentHeight -= cup.getCupHeight();
            cup.makeInvisible();
            cups.remove(cup);
            cups2.remove(cup);
            for (Cup c : cups){
                c.makeInvisible();
            }
            for (int c = 0; c < cups2.size()-1; c++){
                pushCup(c);
                makeVisible();
            }
            for(Cup c : cups){
                cups.add(c);
            }
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
        if (!existeLid(i) && (currentHeight + 1 <= heightTower)){
            int anchoTapa = (2 * i - 1) * SCALE;
            int centroTorre = margen + (widthTower / 2);
            int xCentrada = centroTorre - (anchoTapa / 2);
            // Se usa el nuevo metodo (parecido al de cup) para que queden bien
            //posicionadas las tapas guiandose por lo que hay en la cima de la torre
            int yPixels = calcYPositionLid(i);
            Lid lid = new Lid(i, anchoTapa);
            lid.setPosition(xCentrada, yPixels);
            if (isVisible) lid.makeVisible();
            lids.add(lid);
            currentHeight += 1;
            lastOperationOk = true;
        } else {
            lastOperationOk = false;
        }
    }
    
    /**
     * Simula la caída de una tapa siguiendo las mismas reglas lógicas 
     * que calcYPosition usa para cup
     */
    private int calcYPositionLid(int lidNumber) {
        if(cups.isEmpty() && lids.isEmpty()){
            return heightTower * SCALE - SCALE; // La altura de una tapa es SCALE
        }
        Cup bestContainer = null;
        Cup cupBelow = null;
        // Creamos una taza temporal solo para reutilizar tus métodos de búsqueda
        Cup dummyCup = new Cup(lidNumber, (2 * lidNumber - 1) * SCALE, SCALE);
        for(Cup currentCup : cups){
            if(isCupAccessible(currentCup)){
                if(lidNumber < currentCup.getCupNumber()){
                    Cup innerCup = getCupDirectlyBelow(currentCup);
                    if(innerCup == null){
                        if(bestContainer == null || currentCup.getCupNumber() < bestContainer.getCupNumber()){
                            bestContainer = currentCup;
                            cupBelow = null;
                        }
                    } else {
                        if(lidNumber < innerCup.getCupNumber()){
                            Cup deepestContainer = findProperContainer(innerCup, dummyCup);
                            if(deepestContainer != null && isCupAccessible(deepestContainer)){
                                bestContainer = deepestContainer;
                                cupBelow = getCupDirectlyBelow(deepestContainer);
                            }
                        } else {
                            bestContainer = currentCup;
                            cupBelow = innerCup;
                        }
                    }
                }
            }
        }
        if(bestContainer != null){
            int nivelSuelo;
            if(cupBelow != null){
                nivelSuelo = cupBelow.getYPosition();
            } else {
                nivelSuelo = bestContainer.getYPosition() + bestContainer.getCupHeightPx() - 20; 
            }
            // Se revisa si hay tapas
            int cimaActual = nivelSuelo;
            for (Lid l : lids) {
                if (l.getYPosition() >= bestContainer.getYPosition() && 
                    l.getYPosition() <= bestContainer.getYPosition() + bestContainer.getCupHeightPx()){
                    if (l.getYPosition() < cimaActual) {
                        cimaActual = l.getYPosition();
                    }
                }
            }
            return cimaActual - SCALE;
        }
        // Si no cupo en ninguna taza cae en lo que haya en la cima de la torre
        if(!cups.isEmpty() || !lids.isEmpty()){
            int minY = heightTower * SCALE;
            for (Cup c : cups) if (c.getYPosition() < minY) minY = c.getYPosition();
            for (Lid l : lids) if (l.getYPosition() < minY) minY = l.getYPosition();
            return minY - SCALE;
        }
        return heightTower * SCALE - SCALE;
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
        if (!existeObjeto(o1) || !existeObjeto(o2)){
            lastOperationOk = false;
            return;
        }
    
        Object obj1 = getObject(o1);
        Object obj2 = getObject(o2);
    
        if (obj1 == null || obj2 == null){
            lastOperationOk = false;
            return;
        }   
    
        ArrayList<Object[]> allElements = new ArrayList<>();
    

        for (Cup cup : cups){
            addCupWithThings(allElements, cup);
        }
    
        for (Lid lid : lids){
            allElements.add(new Object[]{null, lid});
        }
    
        int index1 = -1;
        int index2 = -1;
    
        for (int i = 0; i < allElements.size(); i++){
            Object[] unit = allElements.get(i);
        
            if (unit[0] != null && unit[0] == obj1){
                index1 = i;
            }
            if (unit[1] != null && unit[1] == obj1){
                index1 = i;
            }
            if (unit[0] != null && unit[0] == obj2){
                index2 = i;
            }
            if (unit[1] != null && unit[1] == obj2){
                index2 = i;
            }
        }
    
        if (index1 >= 0 && index2 >= 0){
            Object[] temp = allElements.get(index1);
            allElements.set(index1, allElements.get(index2));
            allElements.set(index2, temp);
        
            rebuildTowerFromList(allElements);
            lastOperationOk = true;
        } else {
            lastOperationOk = false;
        }
    }
    
    private void addCupWithThings(ArrayList<Object[]> list, Cup cup){
        list.add(new Object[]{cup, null});
        int yParent = cup.getYPosition();
        int bottomParent = yParent + cup.getCupHeightPx();
    
        for (Cup other : cups){
            if (other != cup){
                int yOther = other.getYPosition();
                int bottomOther = yOther + other.getCupHeightPx();
                if (yOther >= yParent && bottomOther <= bottomParent){
                    boolean isDirect = true;
                    for (Cup between : cups){
                        if (between != cup && between != other){
                            int yBetween = between.getYPosition();
                            int bottomBetween = yBetween + between.getCupHeightPx();
                            if (yBetween >= yParent && bottomBetween <= bottomParent){
                                if (yBetween > yOther && bottomBetween < bottomParent){
                                    isDirect = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (isDirect){
                        addCupWithThings(list, other);
                    }
                }
            }
        }
    }
    
    private Object getObject(String[] o){
        int num = Integer.parseInt(o[1]);
    
        if (o[0].equals("cup")){
            for (Cup cup : cups){
                if (cup.getCupNumber() == num){
                    return cup;
                }
            }
        } else if (o[0].equals("lid")){
            for (Lid lid : lids){
                if (lid.getLidNumber() == num){
                    return lid;
                }
            }
        }
    
        return null;
    }
    
    private void rebuildTowerFromList(ArrayList<Object[]> units){
        for (Cup cup : cups){
            cup.makeInvisible();
        }
        for (Lid lid : lids){
            lid.makeInvisible();
        }
        cups.clear();
        lids.clear();
        currentHeight = 0;
    
        ArrayList<Object[]> sortedUnits = new ArrayList<>(units);
        for (int a = 0; a < sortedUnits.size() - 1; a++){
            for (int b = a + 1; b < sortedUnits.size(); b++){
                if (getUnitNumber(sortedUnits.get(a)) < getUnitNumber(sortedUnits.get(b))){
                    Object[] temp = sortedUnits.get(a);
                    sortedUnits.set(a, sortedUnits.get(b));
                    sortedUnits.set(b, temp);
                }
            }
        }
    
        int centerTower = margen + (widthTower / 2);
        int yBottom = heightTower * SCALE;
    
        for (Object[] unit : sortedUnits){
            if (unit[0] != null){
                Cup cup = (Cup) unit[0];
                int xCentered = centerTower - (cup.getCupWidth() / 2);
                int yPosition = yBottom - cup.getCupHeightPx();
                cup.setPosition(xCentered, yPosition);
            
                if (isVisible){
                    cup.makeVisible();
                }
                cups.add(cup);
                currentHeight += cup.getCupHeight();
                yBottom = yPosition;
            }
        
            if (unit[1] != null){
                Lid lid = (Lid) unit[1];
                int xCentered = centerTower - (lid.getLidWidth() / 2);
                lid.setPosition(xCentered, yBottom - lid.getLidHeight());
            
                if (isVisible){
                    lid.makeVisible();
                }
                lids.add(lid);
                currentHeight += 1;
                yBottom = yBottom - lid.getLidHeight();
            }
        }
    

        orderCupsPosition();
    }
    
    /**
     * Tapa todas las tazas que tienen tapa y luego
     * reorganiza para dejar la torre con las fisicas correctas
     */
    public void cover(){
        boolean huboCambios = false;
        for (Cup cup : cups){
            if (!cup.hasLid() && existeLid(cup.getCupNumber())){
                Lid lid = buscarLid(cup.getCupNumber());
                cup.setLid(lid);
                lid.setCup(cup);
                huboCambios = true;
                int xTapa = cup.getXPosition();
                int yTapa = cup.getYPosition() - SCALE;
                lid.setPosition(xTapa, yTapa);
                for (Cup c : cups) {
                    if (c != cup && c.getYPosition() <= cup.getYPosition()) {
                        c.setPosition(c.getXPosition(), c.getYPosition() - SCALE);
                    }
                }
                for (Lid l : lids) {
                    if (l != lid && l.getYPosition() <= cup.getYPosition()) {
                        l.setPosition(l.getXPosition(), l.getYPosition() - SCALE);
                    }
                }
            }
        }
        if (huboCambios && isVisible) {
            makeVisible();
        }
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
        for (Cup cup : cups) cup.makeInvisible();
        for (Lid lid : lids) lid.makeInvisible();

        for (Cup cup: cups){
            if (isVisible) {
                cup.makeVisible();
            }
            if (cup.hasLid()){
                Lid lid = cup.getLid();
                int centroTorre = margen + (widthTower / 2);
                int xCentrada = centroTorre - (lid.getLidWidth() / 2);
                int yTapa = cup.getYPosition() - SCALE; 
                lid.setPosition(xCentrada, yTapa);
                if (isVisible){
                    lid.makeVisible();
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
        }else if(unit[1] != null){
            return ((Lid) unit[1]).getLidNumber();
        }
        return 0;
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
