public class MainScreen {
    private Map map;
    private String title;
    private Sidebar sidebar;

    public MainScreen(Map map, String title, Sidebar sidebar) {
        this.map = map;
        this.title = title;
        this.sidebar = sidebar;
    }

    public void changeTitle(String title) {
        this.title = title;
    }
}
