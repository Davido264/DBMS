package app.gui;

import java.io.*;
import java.nio.file.Path;
import com.google.gson.*;

public class Settings {
	public static Settings loadFromFile() {
		String localAppDataPath = System.getenv("LOCALAPPDATA");
		String filePath = Path.of(localAppDataPath, "Proyecto-DBMS", "settings.json").toAbsolutePath().toString();
		String dirPath = Path.of(localAppDataPath, "Proyecto-DBMS").toAbsolutePath().toString();
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.serializeNulls()
				.create();
		
		try {
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
				try (FileWriter writer = new FileWriter(filePath)) {
					String content = gson.toJson(Settings.empty);
					writer.write(content);
				} 
				return Settings.empty;
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("Usando configuraciones vacías");
			return Settings.empty;
		}

		String json = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			json = sb.toString();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("Usando configuraciones vacías");
			return Settings.empty;
		}
	
		try {
			Settings settings = gson.fromJson(json, Settings.class);
			return settings;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Usando configuraciones vacías");
			return Settings.empty;
		}
	}
	
	public final static Settings empty = new Settings(true,null,null,false);

	public boolean adminIntegrado;
	public String usuarioAdmin;
	public String claveAdmin;
	public boolean imprimirComandos;
	
	public Settings() {}
	
	public Settings(boolean adminIntegrado, String usuarioAdmin, String claveAdmin, boolean printStatements) {
		this.adminIntegrado = adminIntegrado;
		this.usuarioAdmin = usuarioAdmin;
		this.claveAdmin = claveAdmin;
		this.imprimirComandos = printStatements;
	}
	
}
