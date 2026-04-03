package com.example.doanltmb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.doanltmb.model.Product;
import com.example.doanltmb.utils.HashUtil;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 9;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "email TEXT," +
                "phone TEXT," +
                "address TEXT," +
                "role TEXT)");

        db.execSQL("CREATE TABLE categories (" +
                "category_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category_name TEXT," +
                "description TEXT)");

        db.execSQL("CREATE TABLE products (" +
                "product_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_name TEXT," +
                "description TEXT," +
                "price REAL," +
                "stock INTEGER," +
                "image_url TEXT," +
                "category_id INTEGER)");

        db.execSQL("CREATE TABLE cart (" +
                "cart_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT," +
                "product_name TEXT," +
                "quantity INTEGER)");

        db.execSQL("CREATE TABLE orders (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT," +
                "product_name TEXT," +
                "quantity INTEGER," +
                "price REAL," +
                "order_date DATETIME DEFAULT CURRENT_TIMESTAMP)");

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {

        // HASH TẤT CẢ PASSWORD
        String adminPass = HashUtil.hashPassword("123123");
        String userPass = HashUtil.hashPassword("123456");

        // INSERT SAMPLE USERS
        String sqlUsers = "INSERT INTO users(username,password,role) VALUES " +
                "('admin','" + adminPass + "','admin'), " +
                "('tai','" + userPass + "','customer'), " +
                "('thai','" + userPass + "','customer')";
        db.execSQL(sqlUsers);

        // INSERT SAMPLE CATEGORIES
        String sqlCategories = "INSERT INTO categories(category_name) VALUES " +
                "('Điện thoại'), " +
                "('Laptop'), " +
                "('Tai nghe')";
        db.execSQL(sqlCategories);

        String sqlProducts = "INSERT INTO products(product_name, description, price, stock, image_url, category_id) VALUES " +
                "('iPhone 13', 'Điện Thoại iPhone 13 Chính Hãng.\n" +
                "Màn hình: 6.1 inchs - Super Retina XDR OLED 120Hz.\u200B\n" +
                "CPU : Apple A15 .\n" +
                "Hệ điều hành: IOS 15.\n" +
                "Ram: 4 GB. \n" +
                "Rom: 128GB; 256GB.\n" +
                "Camera trước: 12 Mpx.\n" +
                "Camera sau: 2 Camera 12 x 12 Mpx.\n" +
                "Pin: 3,240 mAh, Sạc Nhanh 20w , Hỗ trợ sạc không dây.\n" +
                "Mạng : Tốc độ 5G.\n" +
                "Sim: 1 Nano Sim & 1 esim.', 15000000, 10,'iphone13', 1), " +
                //SamSung
                "('Samsung Galaxy S23', 'SAMSUNG GALAXY S23 5G CHÍNH HÃNG\n" +
                "Màn hình: 6.1 inchs, Dynamic Amoled 2X.\u200B\n" +
                "CPU : SnapDragon 8 Gen 2 for Galaxy.\n" +
                "Hệ điều hành: Android 15.\n" +
                "Ram: 8GB. \n" +
                "Rom: 128GB, 256GB, 512GB.\n" +
                "Camera trước: 12 Mp.\n" +
                "Camera sau: Chính 50 MP+ 12 MP+ 10 MP.\n" +
                "Pin: 3.900 mAh, Sạc Nhanh 25W.\n" +
                "Mạng : Tốc độ 5G.', 17000000, 8, 'samsungs23', 1), " +
                "('MacBook Air M1', 'Apple laptop', 22000000, 5, 'macbookairm1', 2), " +
                "('Dell XPS 13', 'Dell laptop', 25000000, 4, 'dellxps13', 2), " +
                "('AirPods Pro', 'Tai nghe Apple', 4500000, 15, 'airpodspro',3)";

        db.execSQL(sqlProducts);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS orders");

        onCreate(db);
    }
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedInputPassword = HashUtil.hashPassword(password);
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, hashedInputPassword});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public boolean registerUser(String username, String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=?",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password); // đã hash từ Activity
        values.put("role", "customer");

        long result = db.insert("users", null, values);

        cursor.close();
        db.close();

        return result != -1;
    }
    // GET PRODUCTS (Bản dùng double của bạn)
    public boolean addProduct(String name, double price, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("product_name", name);
        values.put("price", price);
        values.put("image_url", imageUrl);
        // Tạm thời fix cứng các thông tin chưa cần thiết để form đơn giản
        values.put("description", "Mô tả sản phẩm mới");
        values.put("stock", 10);
        values.put("category_id", 1);

        long result = db.insert("products", null, values);
        db.close();

        return result != -1; // Nếu insert thành công result sẽ khác -1
    }
    public ArrayList<Product> getAllProductsList() {

        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT product_name, price, image_url, description FROM products", null
        );

        while (cursor.moveToNext()) {

            String name = cursor.getString(0);
            double price = cursor.getDouble(1);
            String imageUrl = cursor.getString(2);
            String description = cursor.getString(3);

            if (imageUrl == null) imageUrl = "";

            String formattedPrice = String.format("%,.0fđ", price).replace(",", ".");

            list.add(new Product(name, formattedPrice, imageUrl, description));
        }

        cursor.close();
        db.close();

        return list;
    }
    // Khi người dùng nhấn vào sản phẩm sẻ lấy id sản phẩm để lấy thông tin sản phẩm
    public Product getProductById(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT product_name, price, image_url, description FROM products WHERE product_id=?",
                new String[]{String.valueOf(id)}
        );

        Product product = null;

        if (cursor.moveToFirst()) {

            String name = cursor.getString(0);
            double price = cursor.getDouble(1);
            String image = cursor.getString(2);
            String desc = cursor.getString(3);

            String formattedPrice = String.format("%,.0fđ", price).replace(",", ".");

            product = new Product(name, formattedPrice, image, desc);
        }

        cursor.close();
        return product;
    }

    public int updateProduct(String oldName, String newName, String newPrice, String newUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_name", newName);
        values.put("price", newPrice);
        values.put("image_url", newUrl);

        // Dựa vào tên cũ để tìm và cập nhật
        int result = db.update("products", values, "product_name = ?", new String[]{oldName});
        db.close();
        return result;
    }

    public boolean updateUserProfile(String username, String newFullName, String newPhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fullname", newFullName); // Lưu ý: Đảm bảo bảng users có cột này
        values.put("phone", newPhone);

        // Cập nhật dựa trên username của người đang đăng nhập
        int result = db.update("users", values, "username = ?", new String[]{username});
        db.close();
        return result > 0;
    }
    public void deleteProduct(String productName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("products", "product_name = ?", new String[]{productName});
        db.close();
    }

    public String getUserRole(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String role = "user";

        Cursor cursor = db.rawQuery("SELECT role FROM users WHERE username = ?", new String[]{username});

        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
            cursor.close();
        }

        return role;
    }
    public long addProduct(String name, String price, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_name", name);
        values.put("price", price);
        values.put("image_url", imageUrl);
        values.put("category_id", 1);

        long id = db.insert("products", null, values);
        db.close();
        return id;
    }
    //Lấy thông tin người dùng dựa trên username
    public Cursor getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM users WHERE username=?",
                new String[]{username}
        );
    }
    // LẤY DANH SÁCH TÊN DANH MỤC TỪ DB
    public ArrayList<String> getAllCategoryNames() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Tất cả danh mục"); // Thêm dòng này ở vị trí đầu tiên (index 0)

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT category_name FROM categories", null);

        while (cursor.moveToNext()) {
            list.add(cursor.getString(0)); // Lấy tên danh mục thêm vào list
        }

        cursor.close();
        db.close();
        return list;
    }
    // LẤY SẢN PHẨM THEO TÊN DANH MỤC
    public ArrayList<Product> getProductsByCategory(String categoryName) {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Dùng INNER JOIN để đối chiếu category_id giữa 2 bảng
        String query = "SELECT p.product_name, p.price, p.image_url, p.description " +
                "FROM products p INNER JOIN categories c ON p.category_id = c.category_id " +
                "WHERE c.category_name = ?";

        Cursor cursor = db.rawQuery(query, new String[]{categoryName});

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            double price = cursor.getDouble(1);
            String imageUrl = cursor.getString(2);
            String description = cursor.getString(3);

            if (imageUrl == null) imageUrl = "";
            String formattedPrice = String.format("%,.0fđ", price).replace(",", ".");

            list.add(new Product(name, formattedPrice, imageUrl, description));
        }

        cursor.close();
        db.close();
        return list;
    }
    // THÊM SẢN PHẨM VÀO GIỎ HÀNG
    public boolean addToCart(String username, String productName) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;

        // Kiểm tra xem sản phẩm đã có trong giỏ của user này chưa
        Cursor cursor = db.rawQuery(
                "SELECT quantity FROM cart WHERE username=? AND product_name=?",
                new String[]{username, productName}
        );

        if (cursor.moveToFirst()) {
            // Nếu có rồi -> Tăng số lượng lên 1
            int currentQty = cursor.getInt(0);
            ContentValues values = new ContentValues();
            values.put("quantity", currentQty + 1);
            int rows = db.update("cart", values, "username=? AND product_name=?", new String[]{username, productName});
            success = rows > 0;
        } else {
            // Nếu chưa có -> Thêm mới với số lượng là 1
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("product_name", productName);
            values.put("quantity", 1);
            long id = db.insert("cart", null, values);
            success = id != -1;
        }
        cursor.close();
        db.close();
        return success;
    }
    // LẤY DANH SÁCH GIỎ HÀNG CỦA 1 USER
    public Cursor getCartItems(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Dùng INNER JOIN để nối bảng cart và products dựa vào product_name
        String query = "SELECT c.cart_id, c.product_name, p.price, p.image_url, c.quantity " +
                "FROM cart c INNER JOIN products p ON c.product_name = p.product_name " +
                "WHERE c.username = ?";

        return db.rawQuery(query, new String[]{username});
    }
    public void updateCartQuantity(int cartId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);
        db.update("cart", values, "cart_id=?", new String[]{String.valueOf(cartId)});
        db.close();
    }

    // XÓA SẢN PHẨM KHỎI GIỎ NẾU SỐ LƯỢNG VỀ 0
    public void deleteCartItem(int cartId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", "cart_id=?", new String[]{String.valueOf(cartId)});
        db.close();
    }
    // HÀM THANH TOÁN (Chuyển từ Giỏ hàng sang Đơn hàng)
    public boolean checkoutCart(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;

        // 1. Lấy tất cả sản phẩm đang có trong giỏ của người dùng này
        String query = "SELECT c.product_name, c.quantity, p.price " +
                "FROM cart c INNER JOIN products p ON c.product_name = p.product_name " +
                "WHERE c.username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        // Sử dụng Transaction để đảm bảo nếu lỗi giữa chừng thì không bị mất dữ liệu
        db.beginTransaction();
        try {
            if (cursor.moveToFirst()) {
                do {
                    String productName = cursor.getString(0);
                    int quantity = cursor.getInt(1);
                    double price = cursor.getDouble(2);

                    // 2. Thêm từng sản phẩm vào bảng orders
                    ContentValues values = new ContentValues();
                    values.put("username", username);
                    values.put("product_name", productName);
                    values.put("quantity", quantity);
                    values.put("price", price);
                    db.insert("orders", null, values);

                } while (cursor.moveToNext());

                // 3. Thanh toán xong thì XÓA sạch giỏ hàng của user này
                db.delete("cart", "username=?", new String[]{username});

                db.setTransactionSuccessful(); // Xác nhận mọi thao tác thành công
                success = true;
            }
        } finally {
            db.endTransaction(); // Kết thúc Transaction
            cursor.close();
            db.close();
        }

        return success;
    }
}