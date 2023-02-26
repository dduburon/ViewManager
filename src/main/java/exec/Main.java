package exec;

import util.ViewMExecUtil;

public class Main {

    public static void main(String[] args) {
        String mode = null;
        if (args.length >= 1) {
            mode = args[0];
        }
        switch (mode) {
            case "V":
            default:
                ViewMExecUtil.verify();
                break;
        }
    }
}
