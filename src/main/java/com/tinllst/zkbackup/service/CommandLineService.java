package com.tinllst.zkbackup.service;

import lombok.Getter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author tinllst
 */
@Component
public class CommandLineService implements CommandLineRunner {

    @Resource
    private ExportService exportService;
    @Resource
    private ImportService importService;

    @Override
    public void run(String... args) {
        System.out.println("\n\n***********************************************************");
        if (!CommandArgs.checkLegalParamNum(args.length)) {
            System.out.println("try '--help' for more information.");
            return;
        }
        String firstOption = args[0];
        System.out.println("option '" + firstOption + "'");
        CommandArgs command = CommandArgs.findByCommand(firstOption);
        switch (command) {
            case HELP:
                System.out.println("export: java -jar zk-backup.jar -export 'zkPath' 'filePath'");
                System.out.println("import: java -jar zk-backup.jar -import 'filePath'");
                break;
            case EXPORT:
                if (args.length == command.getMaxParamNum()) {
                    long startMillis = System.currentTimeMillis();
                    String nodePath = args[1];
                    String filePath = args[2];
                    System.out.println("start export......... export from nodePath='" + nodePath + "' to filePath='" + filePath + "'");
                    exportService.exportFile(nodePath, filePath);
                    System.out.println("e n d export......... used " + (System.currentTimeMillis() - startMillis) + " ms");
                } else {
                    System.out.println("export incorrect number of parameters");
                }
                break;
            case IMPORT:
                if (args.length == command.getMaxParamNum()) {
                    long startMillis = System.currentTimeMillis();
                    String filePath = args[1];
                    System.out.println("start import......... import from filePath='" + filePath + "'");
                    importService.importFile(filePath);
                    System.out.println("e n d import......... used " + (System.currentTimeMillis() - startMillis) + " ms");
                } else {
                    System.out.println("import incorrect number of parameters");
                }
                break;
            default:
                System.out.println("unrecognized option '" + firstOption + "'");
                System.out.println("try '--help' for more information.");
        }
        System.out.println("***********************************************************\n\n");
    }

    @Getter
    enum CommandArgs {
        /**
         * 命令参数
         */
        HELP("--help", 1),
        EXPORT("-export", 3),
        IMPORT("-import", 2),
        UNKNOWN("unknown", 1);
        private String command;
        private Integer maxParamNum;

        CommandArgs(String command, Integer maxParamNum) {
            this.command = command;
            this.maxParamNum = maxParamNum;
        }

        static CommandArgs findByCommand(String command) {
            return Arrays.stream(values()).filter(args -> args.getCommand().equalsIgnoreCase(command)).findAny().orElse(UNKNOWN);
        }

        static boolean checkLegalParamNum(int argsNum) {
            int max = Arrays.stream(values()).mapToInt(CommandArgs::getMaxParamNum).max().orElse(UNKNOWN.getMaxParamNum());
            int min = Arrays.stream(values()).mapToInt(CommandArgs::getMaxParamNum).min().orElse(UNKNOWN.getMaxParamNum());
            return argsNum >= min && argsNum <= max;
        }
    }
}