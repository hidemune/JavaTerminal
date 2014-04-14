/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaterminal;

import java.awt.Dimension;
import java.io.IOException;

public interface Tty {
        boolean init(Questioner q);
        void close();
        void resize(Dimension termSize, Dimension pixelSize);
        String getName();
        int read(byte[] buf, int offset, int length) throws IOException;
        void write(byte[] bytes) throws IOException;
}

