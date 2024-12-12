package com.example.demo;

import Domain.Cake;
import Domain.CakeFactory;
import Domain.Command;
import Domain.CommandFactory;
import Repository.*;
import Service.Service;
import com.example.demo.UI.UI;

import java.io.InputStream;
import java.util.Properties;


public class Main {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        //creez o clasa Properties pentru a putea gestiona tipurile de repository
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("settings.properties")) {
            //linia de mai sus imi cauta resursele in properties
            if (input == null) {
                System.out.println("File settings.properties not find.");
                return;
            }
            properties.load(input);
        }
        //isi alege tipul de repository si creeaza cu acesta cele necesare pt Service
        String repositoryType = properties.getProperty("repositoryType");
        Repository<Cake> cakeRepo;
        Repository<Command> commandRepo;
        if (repositoryType == null || (!repositoryType.equals("text") && !repositoryType.equals("binary") && !repositoryType.equals("memory")) && !repositoryType.equals("json") && !repositoryType.equals("dataBase")) {
            throw new IllegalArgumentException("Invalid repository type specified in settings.");
        } else {
            switch (repositoryType) {
                case "text":
                    String cakeFile = properties.getProperty("CakesFile");
                    String commandFile = properties.getProperty("CommandsFile");
                    cakeRepo = new TextFileRepository<>(cakeFile, new CakeFactory());
                    commandRepo = new TextFileRepository<>(commandFile, new CommandFactory());
                    break;
                case "binary":
                    cakeFile = properties.getProperty("CakesFile");
                    commandFile = properties.getProperty("CommandsFile");
                    cakeRepo = new BinaryFileRepository<Cake>(cakeFile);
                    commandRepo = new BinaryFileRepository<Command>(commandFile);
                    break;
                case "memory":
                    cakeRepo = new Repository<Cake>();
                    commandRepo = new Repository<Command>();
                    break;
//                case "json":
//                    cakeFile = properties.getProperty("CakesFile");
//                    commandFile = properties.getProperty("CommandsFile");
//                    cakeRepo = new JSONRepository<Cake>(cakeFile, new CakeFactoryJSON());
//                    commandRepo = new JSONRepository<Command>(commandFile, new CommandFactoryJSON());
//                    break;
                case "dataBase":
                    cakeRepo = new DataBaseRepositoryCake();
                    commandRepo = new DataBaseRepositoryCommand();
                    break;
                default:
                    throw new IllegalArgumentException("Repository type not supported: " + repositoryType);
            }
        }

        ///TODO database repository

        Service service = new Service(cakeRepo, commandRepo);
        UI ui = new UI(service);
        ui.run();

    }
}



