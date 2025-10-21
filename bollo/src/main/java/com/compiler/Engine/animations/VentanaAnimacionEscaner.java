package com.compiler.Engine.animations;

import com.compiler.UI.controlador.AnimacionEscanerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;

/**
 * OPCIÓN 1: Crear una ventana independiente para la animación
 */
public class VentanaAnimacionEscaner {
    
    public static void mostrar(String codigoFuente, Stage ownerStage) {
        try {
            // Intentar diferentes rutas según tu estructura de proyecto
            FXMLLoader loader = new FXMLLoader();
            
            // Opción 1: Ruta desde resources bollo\src\main\resources\com\compiler\UI\AnimEscaner.fxml
            java.net.URL fxmlLocation = VentanaAnimacionEscaner.class.getResource("/com/compiler/UI/AnimEscaner.fxml");
            
            // Opción 2: Si está en el mismo paquete
            if (fxmlLocation == null) {
                fxmlLocation = VentanaAnimacionEscaner.class.getResource("AnimEscaner.fxml");
            }
            
            // Opción 3: Ruta directa desde resources
            if (fxmlLocation == null) {
                fxmlLocation = VentanaAnimacionEscaner.class.getResource("/AnimEscaner.fxml");
            }
            
            // Opción 4: Desde el ClassLoader
            if (fxmlLocation == null) {
                fxmlLocation = VentanaAnimacionEscaner.class.getClassLoader().getResource("/bollo/src/main/resources/com/compiler/UI/AnimEscaner.fxml");
            }
            
            if (fxmlLocation == null) {
                throw new java.io.FileNotFoundException("No se pudo encontrar AnimacionEscaner.fxml en ninguna ubicación");
            }
            
            loader.setLocation(fxmlLocation);
            Parent root = loader.load();
            
            // Obtener el controlador y pasarle el código
            AnimacionEscanerController controller = loader.getController();
            controller.setCodigoFuente(codigoFuente);
            
            // Crear la ventana
            Stage stage = new Stage();
            stage.setTitle("Animación del Escáner - Análisis Léxico");
            stage.initModality(Modality.NONE);
            stage.initOwner(ownerStage);
            
            Scene scene = new Scene(root, 1200, 700);
            stage.setScene(scene);
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar la ventana de animación: " + e.getMessage());
        }
    }
}

/**
 * OPCIÓN 2: Integrar en tu controlador principal existente
 * Agrega este método a tu controlador principal
 */
/*
public class MainController {
    
    @FXML
    private CodeArea codeArea; // Tu área de código existente
    
    @FXML
    private void onMostrarAnimacionEscaner() {
        String codigo = codeArea.getText();
        
        if (codigo == null || codigo.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Código vacío");
            alert.setHeaderText("No hay código para analizar");
            alert.setContentText("Por favor, escribe algo de código antes de iniciar la animación.");
            alert.showAndWait();
            return;
        }
        
        // Obtener el Stage actual
        Stage currentStage = (Stage) codeArea.getScene().getWindow();
        
        // Mostrar la ventana de animación
        VentanaAnimacionEscaner.mostrar(codigo, currentStage);
    }
}
*/

/**
 * OPCIÓN 3: Agregar un botón en tu UI principal
 * Agrega esto a tu archivo FXML principal
 */
/*
<Button text="🎬 Animar Escáner" onAction="#onMostrarAnimacionEscaner" 
        style="-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold;"/>
*/

/**
 * OPCIÓN 4: Crear con programación JavaFX pura (sin FXML)
 */
/*
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class AnimacionEscanerProgramatica {
    
    public static void crear(String codigoFuente) {
        Stage stage = new Stage();
        stage.setTitle("Animación del Escáner");
        
        // Crear componentes
        TextFlow textFlowCodigo = new TextFlow();
        TextArea textAreaInfo = new TextArea();
        TextArea textAreaTokens = new TextArea();
        
        // Configurar estilos
        textFlowCodigo.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 10px;");
        textAreaInfo.setEditable(false);
        textAreaTokens.setEditable(false);
        
        // Crear el escáner animado
        EscanerAnimado escanerAnimado = new EscanerAnimado(
            textFlowCodigo, textAreaInfo, textAreaTokens
        );
        escanerAnimado.prepararAnimacion(codigoFuente);
        
        // Crear botones
        Button btnIniciar = new Button("▶ Iniciar");
        Button btnPausar = new Button("⏸ Pausar");
        Button btnDetener = new Button("⏹ Detener");
        Button btnSiguiente = new Button("Siguiente ▶");
        Button btnAnterior = new Button("◀ Anterior");
        
        btnPausar.setDisable(true);
        btnDetener.setDisable(true);
        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        
        // Eventos de botones
        btnIniciar.setOnAction(e -> {
            btnIniciar.setDisable(true);
            btnPausar.setDisable(false);
            btnDetener.setDisable(false);
            escanerAnimado.iniciar();
        });
        
        btnPausar.setOnAction(e -> {
            if (btnPausar.getText().equals("⏸ Pausar")) {
                escanerAnimado.pausar();
                btnPausar.setText("▶ Reanudar");
                btnSiguiente.setDisable(false);
                btnAnterior.setDisable(false);
            } else {
                escanerAnimado.reanudar();
                btnPausar.setText("⏸ Pausar");
                btnSiguiente.setDisable(true);
                btnAnterior.setDisable(true);
            }
        });
        
        btnDetener.setOnAction(e -> {
            escanerAnimado.detener();
            btnIniciar.setDisable(false);
            btnPausar.setDisable(true);
            btnDetener.setDisable(true);
            btnSiguiente.setDisable(false);
            btnAnterior.setDisable(false);
        });
        
        btnSiguiente.setOnAction(e -> escanerAnimado.siguientePaso());
        btnAnterior.setOnAction(e -> escanerAnimado.pasoAnterior());
        
        // Slider de velocidad
        Slider sliderVelocidad = new Slider(100, 2000, 300);
        Label lblVelocidad = new Label("300 ms");
        sliderVelocidad.valueProperty().addListener((obs, old, val) -> {
            int velocidad = val.intValue();
            lblVelocidad.setText(velocidad + " ms");
            escanerAnimado.setVelocidad(velocidad);
        });
        
        // Layout de controles
        HBox controles = new HBox(10);
        controles.setPadding(new Insets(10));
        controles.setAlignment(Pos.CENTER_LEFT);
        controles.setStyle("-fx-background-color: #252525;");
        controles.getChildren().addAll(
            btnIniciar, btnPausar, btnDetener,
            new Separator(javafx.geometry.Orientation.VERTICAL),
            btnAnterior, btnSiguiente,
            new Separator(javafx.geometry.Orientation.VERTICAL),
            new Label("Velocidad:") {{ setStyle("-fx-text-fill: white;"); }},
            sliderVelocidad, lblVelocidad
        );
        
        // Layout de código
        VBox vboxCodigo = new VBox(5);
        Label lblCodigo = new Label("CÓDIGO FUENTE");
        lblCodigo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-color: #2d2d2d;");
        ScrollPane scrollCodigo = new ScrollPane(textFlowCodigo);
        scrollCodigo.setFitToWidth(true);
        scrollCodigo.setFitToHeight(true);
        vboxCodigo.getChildren().addAll(lblCodigo, scrollCodigo);
        VBox.setVgrow(scrollCodigo, Priority.ALWAYS);
        
        // Layout de información
        VBox vboxInfo = new VBox(5);
        Label lblInfo = new Label("INFORMACIÓN DEL PASO ACTUAL");
        lblInfo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-color: #2d2d2d;");
        vboxInfo.getChildren().addAll(lblInfo, textAreaInfo);
        VBox.setVgrow(textAreaInfo, Priority.ALWAYS);
        
        // Layout de tokens
        VBox vboxTokens = new VBox(5);
        Label lblTokens = new Label("TOKENS IDENTIFICADOS");
        lblTokens.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-color: #2d2d2d;");
        textAreaTokens.setText("═══════════════════════════════════════════\n");
        textAreaTokens.appendText("ID  | NOMBRE TOKEN         | LEXEMA\n");
        textAreaTokens.appendText("═══════════════════════════════════════════\n");
        vboxTokens.getChildren().addAll(lblTokens, textAreaTokens);
        VBox.setVgrow(textAreaTokens, Priority.ALWAYS);
        
        // SplitPane inferior
        SplitPane splitInferior = new SplitPane(vboxInfo, vboxTokens);
        splitInferior.setDividerPositions(0.5);
        
        // SplitPane principal
        SplitPane splitPrincipal = new SplitPane(vboxCodigo, splitInferior);
        splitPrincipal.setOrientation(javafx.geometry.Orientation.VERTICAL);
        splitPrincipal.setDividerPositions(0.5);
        
        // Leyenda
        HBox leyenda = new HBox(20);
        leyenda.setAlignment(Pos.CENTER);
        leyenda.setPadding(new Insets(10));
        leyenda.setStyle("-fx-background-color: #252525;");
        leyenda.getChildren().addAll(
            new Label("🟢 Ya procesado") {{ setStyle("-fx-text-fill: lightgreen; -fx-font-weight: bold;"); }},
            new Label("🟡 Carácter actual") {{ setStyle("-fx-text-fill: yellow; -fx-font-weight: bold;"); }},
            new Label("⚪ Por procesar") {{ setStyle("-fx-text-fill: white;"); }}
        );
        
        // Layout principal
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1e1e1e;");
        root.setTop(controles);
        root.setCenter(splitPrincipal);
        root.setBottom(leyenda);
        
        // Escena
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.show();
    }
}
*/

/**
 * EJEMPLO DE USO COMPLETO
 */
/*
public class EjemploUso {
    
    public static void main(String[] args) {
        // En tu método donde quieras iniciar la animación:
        
        String codigoEjemplo = """
            main(){
                int x = 90;
                int y = 70;
                print(x);
            }
        """;
        
        // Opción A: Usando la clase VentanaAnimacionEscaner
        VentanaAnimacionEscaner.mostrar(codigoEjemplo, primaryStage);
        
        // Opción B: Usando la versión programática
        AnimacionEscanerProgramatica.crear(codigoEjemplo);
    }
}
*/