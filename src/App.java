import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.LocalDate;

public class App extends Application {

    private Fridge fridge = new Fridge();

    private GridPane inventoryGrid = new GridPane();
    private VBox itemDetailsBox = new VBox(10);
    private VBox infoBox = new VBox(10);
    private static final double PANEL_WIDTH = 300;
    private static final double PANEL_HEIGHT = 480;

    @Override
    public void start(Stage primaryStage) {

        Font.loadFont(
            getClass().getResourceAsStream("/fonts/PixelifySans-Regular.ttf"),14
        );
        seedTestData();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #bdbdbd;");


        root.setTop(buildTopBar());
        HBox mainContent = new HBox(20); // spacing between panels
        mainContent.setAlignment(Pos.CENTER);

        VBox left = buildInventoryPane();
        VBox center = buildItemDetailsPane();
        VBox right = buildInfoPane();

        mainContent.getChildren().addAll(left, center, right);

        root.setCenter(mainContent);


        refreshInventoryGrid();
        refreshInfoPanel();

        Scene scene = new Scene(root, 1000, 600);

        scene.getStylesheets().add(
            getClass().getResource("/style.css").toExternalForm()
        );

        primaryStage.setTitle("MealCraft");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox buildTopBar() {
        Label title = new Label("MealCraft");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Button inventoryBtn = new Button("inventory");
        Button shoppingBtn = new Button("shopping list");
        Button recipesBtn = new Button("recipes");

        HBox nav = new HBox(10, inventoryBtn, shoppingBtn, recipesBtn);
        nav.setAlignment(Pos.CENTER_RIGHT);

        HBox top = new HBox(title, new Region(), nav);
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);
        top.setAlignment(Pos.CENTER_LEFT);

        return top;
    }

    private VBox buildInventoryPane() {
        Label header = new Label("FOOD STOCK");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button sortBtn = new Button("sort by: a-z");

        HBox headerRow = new HBox(header, new Region(), sortBtn);
        HBox.setHgrow(headerRow.getChildren().get(1), Priority.ALWAYS);

        inventoryGrid.setHgap(8);
        inventoryGrid.setVgap(8);

        Button addItemBtn = new Button("add item");
        addItemBtn.setMaxWidth(Double.MAX_VALUE);

        VBox left = new VBox(10, headerRow, inventoryGrid, addItemBtn);
        left.setPadding(new Insets(10));
        left.setPrefSize(PANEL_WIDTH, PANEL_HEIGHT);
        left.setMinSize(PANEL_WIDTH, PANEL_HEIGHT);
        left.setMaxSize(PANEL_WIDTH, PANEL_HEIGHT);
        left.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: #e6e6e6;");

        return left;
    }

    private VBox buildItemDetailsPane() {
        Label header = new Label("ITEM DETAILS");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        itemDetailsBox.setPadding(new Insets(10));
        itemDetailsBox.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: #e6e6e6;");
        itemDetailsBox.setPrefSize(PANEL_WIDTH, PANEL_HEIGHT);
        itemDetailsBox.setMinSize(PANEL_WIDTH, PANEL_HEIGHT);
        itemDetailsBox.setMaxSize(PANEL_WIDTH, PANEL_HEIGHT);

        VBox center = new VBox(10, header, itemDetailsBox);
        center.setPrefSize(PANEL_WIDTH, PANEL_HEIGHT);
        center.setMinSize(PANEL_WIDTH, PANEL_HEIGHT);
        center.setMaxSize(PANEL_WIDTH, PANEL_HEIGHT);

        return center;
    }


    private VBox buildInfoPane() {
        Label header = new Label("INFO");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: #e6e6e6;");
        infoBox.setPrefSize(PANEL_WIDTH, PANEL_HEIGHT);
        infoBox.setMinSize(PANEL_WIDTH, PANEL_HEIGHT);
        infoBox.setMaxSize(PANEL_WIDTH, PANEL_HEIGHT);

        VBox right = new VBox(10, header, infoBox);
        right.setPrefSize(PANEL_WIDTH, PANEL_HEIGHT);
        right.setMinSize(PANEL_WIDTH, PANEL_HEIGHT);
        right.setMaxSize(PANEL_WIDTH, PANEL_HEIGHT);

        return right;
    }


    private void refreshInventoryGrid() {
        inventoryGrid.getChildren().clear();

        int col = 0;
        int row = 0;

        for (FoodItem item : fridge.getInventoryByName().values()) {
            Button slot = buildInventorySlot(item);
            inventoryGrid.add(slot, col, row);

            col++;
            if (col == 5) {
                col = 0;
                row++;
            }
        }
    }

    private Button buildInventorySlot(FoodItem item) {
        ImageView icon = new ImageView();

        if (item.getImgFilePath() != null) {
            try {
                Image img = new Image("file:" + item.getImgFilePath());
                icon.setImage(img);
                icon.setFitWidth(40);
                icon.setFitHeight(40);
            } catch (Exception ignored) {}
        }

        Label qty = new Label(String.valueOf((int) item.getQuantity()));
        StackPane stack = new StackPane(icon, qty);
        StackPane.setAlignment(qty, Pos.BOTTOM_RIGHT);

        Button btn = new Button();
        btn.setGraphic(stack);
        btn.setPrefSize(60, 60);

        btn.setOnAction(e -> showItemDetails(item));

        return btn;
    }

    private void showItemDetails(FoodItem item) {
        itemDetailsBox.getChildren().clear();

        ImageView imgView = new ImageView();
        if (item.getImgFilePath() != null) {
            try {
                imgView.setImage(new Image("file:" + item.getImgFilePath()));
                imgView.setFitWidth(80);
                imgView.setFitHeight(80);
            } catch (Exception ignored) {}
        }

        Label name = new Label(item.getName());
        Label category = new Label("Category: " + item.getCategory());
        Label qty = new Label("Quantity: " + item.getQuantity() + " " + item.getUnit());
        Label exp = new Label("Expires: " + item.getExpirationDate());

        Button editBtn = new Button("edit item");

        itemDetailsBox.getChildren().addAll(imgView, name, category, qty, exp, editBtn);
    }

    private void refreshInfoPanel() {
        infoBox.getChildren().clear();

        Label useSoonLabel = new Label("USE SOON:");
        useSoonLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        for (FoodItem item : fridge.getExpiringSoon(3)) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(), item.getExpirationDate());
            Label l = new Label("• " + item.getName() + " (expires " + days + " day)");
            infoBox.getChildren().add(l);
        }

        Separator sep = new Separator();

        Label lowStockLabel = new Label("LOW STOCK:");
        lowStockLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        for (FoodItem item : fridge.getInventoryByName().values()) {
            if (item.getQuantity() <= 2) {
                Label l = new Label("• " + item.getName() + " (x" + (int) item.getQuantity() + ")");
                infoBox.getChildren().add(l);
            }
        }

        infoBox.getChildren().add(0, useSoonLabel);
        infoBox.getChildren().add(sep);
        infoBox.getChildren().add(lowStockLabel);
    }

    private void seedTestData() {
        fridge.addFood(new FoodItem("Watermelon", 1, "piece", Category.FRUITS_VEGETABLES,
                LocalDate.now().plusDays(5), "fooditem-images/fooditem-images/melon.png"));

        fridge.addFood(new FoodItem("Milk", 1, "carton", Category.DAIRY_EGGS,
                LocalDate.now().plusDays(1), "fooditem-images/milk.png"));

        fridge.addFood(new FoodItem("Chicken", 1, "lb", Category.PROTEINS,
                LocalDate.now().plusDays(3), "fooditem-images/chicken.png"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
