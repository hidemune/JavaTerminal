/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaterminal;

public interface Questioner{
        String questionVisible(String question, String defValue);
       
        String questionHidden(String string);

        void showMessage(String message);
}

