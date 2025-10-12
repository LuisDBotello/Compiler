package com.compiler.UI.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator; // Para ordenar archivos
import com.compiler.Engine.*;

public class EditorController {
    @FXML
    private ScrollPane scrollLexico, scrollSintactico, scrollSemantico, scrollCodigoGenerado;

    @FXML
    private TabPane tabResultados; 
    private CodeArea codeAreaLexico, codeAreaSintactico, codeAreaSemantico, codeAreaCodigoGenerado;

    @FXML
    private VBox contenedorEditor;

    @FXML
    private SplitPane splitVertical;
    @FXML
    private TreeView<File> treeArchivos; // CAMBIO: Ahora maneja objetos File
    private int tamanoFuente = 14; // Tamaño inicial
    
    // @FXML
    // private VBox panelIzquierdo; // Este VBox no se usa directamente en el código que mostraste,
                                   // pero si lo usaras para algo más, se quedaría.

    @FXML
    private VBox panelDerecho; // CAMBIO: Renombrado a 'panelDerecho' para coincidir con fx:id en FXML
                               // Si tu FXML usa fx:id="vboxCodigo", entonces manten 'vboxCodigo' aquí.
                               // Asumo que tu FXML tiene fx:id="panelDerecho" para el VBox principal del editor.

    private CodeArea codeArea; // El campo no es @FXML porque se crea programáticamente

    private File carpetaBase = new File("C:/Users/luisd/Documents/Compilador/bollo"); // CAMBIO: Ajustada la ruta base si "bollo" es la raíz del proyecto


        
    @FXML
    private void initialize() {
        // 1. Inicializar CodeArea principal
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.getStyleClass().add("code-area");
        actualizarTamanoFuente();
        
        // Agregar al contenedor del editor
        contenedorEditor.getChildren().add(codeArea);
        VBox.setVgrow(codeArea, Priority.ALWAYS);
        
        // 2. Inicializar CodeAreas de resultados (solo lectura)
        codeAreaLexico = crearCodeAreaResultado();
        codeAreaSintactico = crearCodeAreaResultado();
        codeAreaSemantico = crearCodeAreaResultado();
        codeAreaCodigoGenerado = crearCodeAreaResultado();
        
        // Agregar a los ScrollPanes
        scrollLexico.setContent(codeAreaLexico);
        scrollSintactico.setContent(codeAreaSintactico);
        scrollSemantico.setContent(codeAreaSemantico);
        scrollCodigoGenerado.setContent(codeAreaCodigoGenerado);

        codeAreaLexico.setStyle("-fx-font-size: 14px;");
        
        cargarExplorador(carpetaBase);
        configurarClickArchivo();
    }

    /**
     * Carga el TreeView con la estructura de archivos y carpetas de la carpeta base.
     *
     * @param carpeta El directorio raíz desde el cual se construirá el explorador.
     */
    private void cargarExplorador(File carpeta) {
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            System.err.println("La carpeta base no existe o no es un directorio: " + carpeta.getAbsolutePath());
            return;
        }
        TreeItem<File> rootItem = crearNodo(carpeta);
        treeArchivos.setRoot(rootItem);
        treeArchivos.setShowRoot(true); // Mostrar el nodo raíz (la carpeta base)
        rootItem.setExpanded(true); // Expandir el nodo raíz por defecto
    }

    /**
     * Crea nodos recursivos para el TreeView a partir de un objeto File.
     *
     * @param file El archivo o directorio actual para el cual se creará un nodo.
     * @return Un TreeItem<File> que representa el archivo o directorio.
     */
    private TreeItem<File> crearNodo(File file) {
        // Creamos un TreeItem usando el objeto File completo
        TreeItem<File> item = new TreeItem<>(file);
        
        // Configura el nombre visible en el TreeView
        // Si es la carpeta base, muestra el nombre completo o "Proyecto"
        if (file.equals(carpetaBase)) {
            item.setValue(new File("Proyecto")); // Puedes cambiar esto por file.getName() o el nombre que prefieras
        } else {
            item.setValue(file); // Por defecto, TreeItem.getValue() mostrará file.toString() o file.getName()
        }

        if (file.isDirectory()) {
            // Se expande por defecto para directorios (puedes cambiarlo)
            item.setExpanded(false); 
            File[] children = file.listFiles();
            if (children != null) {
                // Opcional: ordenar archivos y carpetas alfabéticamente
                // Arrays.sort(children, Comparator.comparing(File::getName)); // Requiere java.util.Arrays
                for (File child : children) {
                    item.getChildren().add(crearNodo(child));
                }
                // Si quieres ordenar después de añadir todos:
                item.getChildren().sort(Comparator.comparing(fItem -> fItem.getValue().getName()));
            }
        }
        return item;
    }

    /**
     * Configura un listener para el TreeView que carga el contenido de un archivo
     * seleccionado en el CodeArea.
     */
    private void configurarClickArchivo() {
        treeArchivos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                // Obtenemos el objeto File directamente del TreeItem
                File f = newSel.getValue(); 
                
                if (f.isFile()) {
                    try {
                        // Leemos el contenido del archivo usando su ruta
                        String contenido = new String(Files.readAllBytes(f.toPath()));
                        codeArea.replaceText(contenido);
                        // Opcional: Establecer el título de la ventana o una etiqueta con el nombre del archivo
                        // Stage stage = (Stage) panelDerecho.getScene().getWindow();
                        // stage.setTitle("Visual Dannys Editor - " + f.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Aquí puedes mostrar una alerta al usuario sobre el error de lectura
                    }
                }
            }
        });
    }

    /**
     * Acción para el botón "Refrescar Explorador".
     * Vuelve a cargar la estructura de archivos desde la carpeta base.
     */
    @FXML
    private void refrescarExplorador() {
        cargarExplorador(carpetaBase);
    }

    /**
     * Acción para el botón "Compilar".
     * Muestra el contenido actual del CodeArea en la consola.
     * Aquí se invocaría la lógica de tu compilador.
     */
    @FXML
    private void compilarCodigo() {
        System.out.println("Compilando código:\n" + codeArea.getText());
        // Lógica para invocar tu compilador
    }

    /**
     * Acción para el botón "Guardar".
     * Guarda el contenido del CodeArea en el archivo actualmente seleccionado en el TreeView.
     */
    @FXML
    private void guardarCodigo() {
        TreeItem<File> seleccionado = treeArchivos.getSelectionModel().getSelectedItem();
        
        if (seleccionado != null) {
            File f = seleccionado.getValue(); // Obtenemos el objeto File directamente
            
            if (f.isFile()) {
                try {
                    Files.write(f.toPath(), codeArea.getText().getBytes());
                    System.out.println("Archivo guardado: " + f.getAbsolutePath());
                    // Puedes mostrar una notificación de éxito aquí
                } catch (IOException e) {
                    e.printStackTrace();
                    // Aquí puedes mostrar una alerta de error al usuario
                }
            } else {
                System.out.println("No se puede guardar: No se ha seleccionado un archivo válido.");
                // Alerta: "Seleccione un archivo para guardar."
            }
        } else {
            System.out.println("No se puede guardar: No se ha seleccionado ningún archivo.");
            // Alerta: "Seleccione un archivo para guardar."
        }
    }
    @FXML
    private void aumentarTexto() {
        tamanoFuente += 2;
        if (tamanoFuente > 30) { // Límite máximo
            tamanoFuente = 30;
        }
        actualizarTamanoFuente();
    }

    @FXML
    private void disminuirTexto() {
        tamanoFuente -= 2;
        if (tamanoFuente < 10) { // Límite mínimo
            tamanoFuente = 10;
        }
        actualizarTamanoFuente();
    }

    @FXML
    private void ejecutarPrograma() {
        // Tu lógica para ejecutar el programa
        System.out.println("Ejecutando programa...");
        
        // Ejemplo de implementación:
        // 1. Verificar que el código esté compilado
        // 2. Ejecutar el programa compilado
        // 3. Mostrar la salida en consola o en un área de texto
    }
    @FXML
    private void actualizarTamanoFuente() {
        codeArea.setStyle(
            "-fx-font-size: " + tamanoFuente + "px;" +
            "-fx-highlight-fill: #06360095;" +
            "-fx-highlight-text-fill: #ffffff;" +
            "-rtfx-selection-background-color: #06360095;"
        );
    }

    private CodeArea crearCodeAreaResultado() {
    CodeArea ca = new CodeArea();
    ca.setEditable(false); // Solo lectura
    ca.getStyleClass().add("code-area-resultado");
    ca.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 12px;");
    return ca;
    }

    // Métodos para actualizar resultados
    @FXML
    private void escanearCodigo() {
        // Tu lógica de escaneo
        Escaner escaner = new Escaner(codeArea.getText());

        if (codeArea.getText().equals("")){
            codeAreaLexico.setStyle("-fx-background-color: #0d1117"); 
            codeAreaLexico.replaceText("");
            return;
        }
        codeAreaLexico.setStyle("-fx-background-color: #02541392;");
        codeAreaLexico.replaceText("Tokens encontrados:\n...");
        escaner.Scan(); // Escanea el texto del CodeArea principal
        escaner.WriteRun(codeAreaLexico); // Muestra los tokens en codeAreaLexico
        if (escaner.gethasError())
            codeAreaLexico.setStyle("-fx-text-fill: #F74E2D;-fx-background-color: #3b1515ff;");


    }

    @FXML
    private void parsearCodigo() {
        // Tu lógica de parsing
        codeAreaSintactico.replaceText("Árbol sintáctico:\n...");
    }

    @FXML
    private void analizarSemantico() {
        // Tu lógica semántica
        codeAreaSemantico.replaceText("Análisis semántico:\n...");
    }

    @FXML
    private void generarEnsamblador() {
        // Tu lógica de generación
        codeAreaCodigoGenerado.replaceText("Código ensamblador:\n...");
    }

    @FXML
    private void generarObjeto() {
        // Tu lógica de generación de objeto
        codeAreaCodigoGenerado.appendText("\n\nCódigo objeto:\n...");
    }
}