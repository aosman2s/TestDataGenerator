package de.aspera.dataexport.cmd;

import org.springframework.stereotype.Component;

@Component
public class QuitCommand implements CommandRunnable {

    @Override
    public void run() {
        System.out.println("quit. good bye!");
        System.exit(0);
    }
}
