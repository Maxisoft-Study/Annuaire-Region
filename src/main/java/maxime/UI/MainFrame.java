package maxime.UI;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.image.WebImage;
import com.alee.extended.layout.HorizontalFlowLayout;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.extended.painter.TitledBorderPainter;
import com.alee.extended.panel.BorderPanel;
import com.alee.extended.panel.FlowPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.statusbar.WebMemoryBar;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.zoom.ZoomTransitionEffect;
import com.alee.extended.transition.effects.zoom.ZoomType;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebMenu;
import com.alee.laf.menu.WebMenuBar;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import com.alee.laf.tree.TreeSelectionStyle;
import com.alee.laf.tree.WebTree;
import com.alee.laf.tree.WebTreeModel;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import com.google.common.collect.Lists;
import maxime.BaseClass.BaseClass;
import maxime.BaseClass.Commune;
import maxime.BaseClass.Departement;
import maxime.BaseClass.Region;
import maxime.Const;
import maxime.ResourceLoader;
import maxime.SearchProcessor.SearchProcessor;
import maxime.UI.ResultTree.*;
import maxime.UI.ResultTree.WrappedBaseClass.WrappedBaseClass;
import maxime.UI.ResultTree.WrappedBaseClass.WrappedCommune;
import maxime.UI.ResultTree.WrappedBaseClass.WrappedDepartement;
import maxime.UI.ResultTree.WrappedBaseClass.WrappedRegion;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class MainFrame extends WebFrame implements Const {
    final private WebTextField searchbar;
    final private WebMenuBar menuBar;
    final private CommunePointHelper communePointHelper;
    final private ResourceLoader resourceLoader;
    final private SearchProcessor searchProcessor;
    final private ArrayList<CommunePointHelper.CommunePoint> communePoints;
    private final ComponentTransition componentTransition;
    private WebTable communeTable;
    private WebScrollPane searchResult;
    private WebTree resultTree;
    private WebDecoratedImage carteview;
    private JLayeredPane imgcontener;
    private Region lastRegion;
    private Departement lastDepartement;
    private Commune lastCommune;
    //les tableau de propriéte
    private WebTable regionTable;
    private WebTable departementTable;
    private MyTreeNode rootNode;
    private SearchProcessor.ResultSet lastResult = SearchProcessor.ResultSet.EMPTY_RESULT;
    private HashMap<Class<? extends BaseClass>, MyTreeNodeResultType> mapNodeResultType;
    private WebTabbedPane tabbedPane;
    private int imageWidth;
    private int imageHeight;
    private Rectangle imgbounds;


    private int autoExpendResultNbr = 10;
    public boolean enableAnimation = true;


    public MainFrame(ResourceLoader loader) throws HeadlessException, IOException {
        super("Region");
        this.resourceLoader = loader;
        this.searchProcessor = new SearchProcessor(this.resourceLoader);
        this.mapNodeResultType = new HashMap<Class<? extends BaseClass>, MyTreeNodeResultType>();
        this.communePointHelper = new CommunePointHelper(this.resourceLoader);
        this.communePoints = new ArrayList<CommunePointHelper.CommunePoint>();
        this.componentTransition = new ComponentTransition();

        ZoomTransitionEffect effect = new ZoomTransitionEffect();
        effect.setMinimumSpeed(0.03f);
        effect.setSpeed(0.1f);
        effect.setType(ZoomType.zoomOut);
        componentTransition.setTransitionEffect(effect);

        setShowMaximizeButton(false);
        setShowResizeCorner(false);
        setResizable(false);
        setUndecorated(true);
        setIconImages(WebLookAndFeel.getImages());
        setDefaultCloseOperation(WebFrame.DISPOSE_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);


        this.menuBar = createMenuBar();
        setJMenuBar(menuBar);
        this.searchbar = createSearchBar();

        updateSearchResultTree();
        this.resultTree = createSearchResultTree();

        add(createContent());

        setupGlobalHotkeys();

        this.pack();
        updateTables();
        updateWindow();
        this.setLocationRelativeTo(null); // centre

    }

    private void setupGlobalHotkeys() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                            if (!MainFrame.this.searchbar.hasFocus()) {
                                MainFrame.this.searchbar.requestFocus();
                            }
                            return true;
                        }
                        return false;
                    }
                });
    }

    private synchronized void updateTables() {
        updateCommuneTable();
        updateRegionTable();
        updateDepartementTable();
    }

    private void updateDepartementTable() {
        if (lastDepartement == null) {
            tabbedPane.setEnabledAt(2, false); //disable Tab
            return;
        }
        tabbedPane.setEnabledAt(2, true);
        String[][] data = lastDepartement.getProprieties();
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[i].length; ++j) {
                departementTable.setValueAt(data[i][j], i, j);
            }
        }
    }

    private void updateCommuneTable() {
        if (lastCommune == null) {
            tabbedPane.setEnabledAt(1, false);//disable Tab
            return;
        }
        tabbedPane.setEnabledAt(1, true);
        String[][] data = lastCommune.getProprieties();
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[i].length; ++j) {
                communeTable.setValueAt(data[i][j], i, j);
            }
        }
    }

    private void updateRegionTable() {
        String[][] data = lastRegion.getProprieties();
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[i].length; ++j) {
                regionTable.setValueAt(data[i][j], i, j);
            }
        }
    }

    private void updateWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                carteview.validate();
                carteview.repaint();
                MainFrame.this.validate();
                MainFrame.this.repaint();
                MainFrame.this.pack();

            }
        });
    }

    private synchronized void updateCommunePointHelper() {
        communePointHelper.setImageWidth(imageWidth);
        communePointHelper.setImageHeight(imageHeight);
        communePointHelper.setRegion(lastRegion);
        communePointHelper.setImgbound(imgbounds);
    }

    private synchronized void changeRegion(final Region region) {
        if (region == this.lastRegion) {
            return;
        }
        componentTransition.fireTransitionFinished();
        lastRegion = region;
        final Image image = region.getCarte();
        TooltipManager.removeTooltips(carteview);
        this.carteview = createCarteView();
        this.carteview.setImage(image);
        imageWidth = image.getWidth(null);
        imageHeight = image.getHeight(null);
        imgbounds = new Rectangle((MAX_IMG_WIDTH - imageWidth) / 2, (MAX_IMG_HEIGHT - imageHeight) / 2, imageWidth + 5, imageHeight + 5);
        carteview.setBounds(imgbounds);
        if (enableAnimation) {
            componentTransition.performTransition(this.carteview);
        } else {
            componentTransition.setContent(this.carteview);
        }

        TooltipManager.setTooltip(carteview, region.getNom(), TooltipWay.trailing);
        clearCommunesPoints();
        updateCommunePointHelper();
        updateWindow();
    }

    private BorderPanel createContent() throws IOException {
        return new BorderPanel(new WebPanel(new VerticalFlowLayout(5, 5)) {
            {
                setAlignmentX(MainFrame.CENTER_ALIGNMENT);
                setAlignmentY(MainFrame.CENTER_ALIGNMENT);
                setMargin(5);

                final TitledBorderPainter titledBorderPainter = new TitledBorderPainter();
                titledBorderPainter.setTitleOffset(10);
                titledBorderPainter.setRound(Math.max(0, this.getRound() - 2));
                setPainter(titledBorderPainter);

                add(MainFrame.this.searchbar);

                add(new WebPanel(new HorizontalFlowLayout(3, false)) {
                    {
                        setAlignmentX(MainFrame.CENTER_ALIGNMENT);
                        setAlignmentY(MainFrame.CENTER_ALIGNMENT);
                        MainFrame.this.carteview = createCarteView();
                        imgcontener = (JLayeredPane) add(new JLayeredPane() {
                            {
                                setPreferredSize(new Dimension(MAX_IMG_WIDTH + 5, MAX_IMG_HEIGHT + 5));
                                carteview.setBounds(0, 0, carteview.getWidth(), carteview.getHeight());
                                componentTransition.setContent(carteview);
                                componentTransition.setBounds(0, 0, MAX_IMG_WIDTH + 5, MAX_IMG_HEIGHT + 5);
                                add(componentTransition);
                                //add(carteview, 1);
                            }
                        })
                        ;

                        changeRegion(MainFrame.this.resourceLoader.findRegion(9));


                        add(new WebSeparator(WebSeparator.VERTICAL));

                        tabbedPane = new WebTabbedPane();
                        tabbedPane.setTabPlacement(WebTabbedPane.TOP);

                        MainFrame.this.searchResult = createSearchResultContent(300, MainFrame.this.resultTree);
                        tabbedPane.addTab("Resultats recherche", searchResult);
                        tabbedPane.addTab("Commune", createComuneTab());
                        tabbedPane.addTab("Departement", createDepartementTab());
                        tabbedPane.addTab("Region", createRegionTab());
                        add(tabbedPane);
                    }
                });
            }
        }, 10);
    }


    private WebDecoratedImage createCarteView() {
        return new WebDecoratedImage() {
            {
                setVerticalAlignment(WebDecoratedImage.CENTER);
                setHorizontalAlignment(WebDecoratedImage.CENTER);
            }
        };
    }

    private WebTextField createSearchBar() {
        return new WebTextField("", 1) {
            {
                putClientProperty(GroupPanel.FILL_CELL, true);
                setInputPrompt("Recherche ...");
                TooltipManager.setTooltip(this, "La Region, le Departement ou la Commune a chercher. (ctrl+F)", TooltipWay.down);
                setLeadingComponent(new WebImage(resourceLoader.loadImage("search.png")));
                addCaretListener(new CaretListener() {
                    private String previousText;

                    @Override
                    public void caretUpdate(CaretEvent e) {
                        if (getText().equals(previousText)) {
                            return;
                        }
                        previousText = getText();
                        MainFrame.this.lastResult = MainFrame.this.searchProcessor.search(getText());
                        updateSearchResultTree();
                        // auto focus les resultats de recherches
                        MainFrame.this.tabbedPane.setSelectedIndex(0);
                    }
                });
            }
        };
    }

    private WebScrollPane createSearchResultContent(int width, WebTree tree) throws IOException {
        final WebScrollPane scrollPane = new WebScrollPane(tree, false);
        scrollPane.setPreferredSize(new Dimension(width, 1));
        return scrollPane;
    }

    private MyTreeNodeResultType createNodeResultType(Class<? extends BaseClass> klass) {
        MyTreeNodeResultType ret = new MyTreeNodeResultType(klass.getSimpleName() + "s");
        mapNodeResultType.put(klass, ret);
        return ret;
    }

    private synchronized void updateSearchResultTree() {
        if (rootNode == null) { // construct time
            MyTreeNodeRoot root = new MyTreeNodeRoot();
            root.add(createNodeResultType(Region.class));
            root.add(createNodeResultType(Departement.class));
            root.add(createNodeResultType(Commune.class));
            this.rootNode = root;
        }
        int pos = 0;
        MyTreeNodeResultType regionsnode = mapNodeResultType.get(Region.class);
        regionsnode.removeAllChildren();

        boolean isnodeChild = rootNode.isNodeChild(regionsnode);
        final boolean regionsempty = lastResult.regions.isEmpty();
        if (regionsempty) {
            if (isnodeChild) {
                rootNode.remove(regionsnode);
            }
        } else {
            if (!isnodeChild) {
                rootNode.insert(regionsnode, pos);
                pos += 1;
            }
            for (Region region : lastResult.regions) {
                regionsnode.add(new MyTreeNodeLeaf(new WrappedRegion(region)));
            }
        }

        MyTreeNodeResultType departementsnode = mapNodeResultType.get(Departement.class);
        departementsnode.removeAllChildren();
        isnodeChild = rootNode.isNodeChild(departementsnode);
        final boolean departementsempty = lastResult.departements.isEmpty();
        if (departementsempty) {
            if (isnodeChild) {
                rootNode.remove(departementsnode);
            }
        } else {
            if (!isnodeChild) {
                rootNode.insert(departementsnode, pos);
                pos += 1;
            }
            for (Departement departement : lastResult.departements) {
                departementsnode.add(new MyTreeNodeLeaf(new WrappedDepartement(departement)));
            }
        }


        MyTreeNodeResultType communesnode = mapNodeResultType.get(Commune.class);
        communesnode.removeAllChildren();
        isnodeChild = rootNode.isNodeChild(communesnode);
        final boolean communesempty = lastResult.communes.isEmpty();
        if (communesempty) {
            if (isnodeChild) {
                rootNode.remove(communesnode);
            }
        } else {
            if (!isnodeChild) {
                rootNode.insert(communesnode, pos);
            }
            for (Commune commune : lastResult.communes) {
                communesnode.add(new MyTreeNodeLeaf(new WrappedCommune(commune)));
            }
        }
        if (resultTree != null) {
            ((WebTreeModel) resultTree.getModel()).reload();
            updateWindow();
            if (communesempty && departementsempty) {
                resultTree.expandPath(new TreePath(regionsnode.getPath()));
            } else if (regionsempty && departementsempty) {
                resultTree.expandPath(new TreePath(communesnode.getPath()));
            } else if (regionsempty && communesempty) {
                resultTree.expandPath(new TreePath(departementsnode.getPath()));
            } else if (autoExpendResultNbr > 0) {
                if (!regionsempty && lastResult.regions.size() <= autoExpendResultNbr) {
                    resultTree.expandPath(new TreePath(regionsnode.getPath()));
                }
                if (!departementsempty && lastResult.departements.size() <= autoExpendResultNbr) {
                    resultTree.expandPath(new TreePath(departementsnode.getPath()));
                }
                if (!communesempty && lastResult.communes.size() <= autoExpendResultNbr) {
                    resultTree.expandPath(new TreePath(communesnode.getPath()));
                }
            }
        }
    }

    private synchronized void clearCommunesPoints() {
        Iterator<CommunePointHelper.CommunePoint> it = this.communePoints.iterator();
        while (it.hasNext()) {
            CommunePointHelper.CommunePoint point = it.next();
            TooltipManager.removeTooltips(point);
            imgcontener.remove(point);
            it.remove();
        }
        updateWindow();
    }


    private WebScrollPane createRegionTab() {
        return new WebScrollPane(

                new WebPanel(new VerticalFlowLayout(5, 5)) {
                    {
                        add(new WebTable(new String[][]{
                                {"nom", "sample"},
                                {"nombre de Departements", "sample"},
                                {"nombre de Communes", "sample"},
                                {"getSuperficie", "sample"},
                                {"getPopulation", "sample"},
                                {"densité", "sample"},
                        }, new String[]{"", ""}) {
                            {
                                MainFrame.this.regionTable = this;
                                setEditable(false);
                                setTableHeader(null);
                            }
                        });

                        add(new WebButton("Recherche Départements", new ImageIcon(resourceLoader.loadImage("search.png"))){
                            {
                                addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        MainFrame.this.lastResult = new SearchProcessor.ResultSet(SearchProcessor.ResultSet.EMPTY_RESULT.regions, lastRegion.getDepartements(), SearchProcessor.ResultSet.EMPTY_RESULT.communes);
                                        updateSearchResultTree();
                                        MainFrame.this.tabbedPane.setSelectedIndex(0);
                                    }
                                });
                            }
                        });

                        add(new WebButton("Recherche Communes", new ImageIcon(resourceLoader.loadImage("search.png"))){
                            {
                                addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        ArrayList<Commune> communes = new ArrayList<Commune>();
                                        for(Departement departement : lastRegion.getDepartements()){
                                            communes.addAll(departement.getCommunes());
                                        }
                                        MainFrame.this.lastResult = new SearchProcessor.ResultSet(SearchProcessor.ResultSet.EMPTY_RESULT.regions, SearchProcessor.ResultSet.EMPTY_RESULT.departements, communes);
                                        updateSearchResultTree();
                                        MainFrame.this.tabbedPane.setSelectedIndex(0);
                                    }
                                });
                            }
                        });
                    }
                }
        , false);
    }

    private WebScrollPane createDepartementTab() {
        return new WebScrollPane(new WebPanel(new VerticalFlowLayout(5, 5)) {
            {
                add(new WebTable(new String[][]{
                        {"nom", "sample"},
                        {"code", "sample"},
                        {"region", "sample"},
                        {"nombre de Communes", "sample"},
                        {"superficie", "sample"},
                        {"population", "sample"},
                        {"densité", "sample"},
                }, new String[]{"", ""}) {
                    {
                        MainFrame.this.departementTable = this;
                        setEditable(false);
                        setTableHeader(null);
                    }
                });

                add(new WebButton("Recherche Region", new ImageIcon(resourceLoader.loadImage("search.png"))){
                    {
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.lastResult = new SearchProcessor.ResultSet(Arrays.asList(lastDepartement.getRegion()), SearchProcessor.ResultSet.EMPTY_RESULT.departements, SearchProcessor.ResultSet.EMPTY_RESULT.communes);
                                updateSearchResultTree();
                                MainFrame.this.tabbedPane.setSelectedIndex(0);
                            }
                        });
                    }
                });

                add(new WebButton("Recherche Communes", new ImageIcon(resourceLoader.loadImage("search.png"))){
                    {
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.lastResult = new SearchProcessor.ResultSet(SearchProcessor.ResultSet.EMPTY_RESULT.regions, SearchProcessor.ResultSet.EMPTY_RESULT.departements, lastDepartement.getCommunes());
                                updateSearchResultTree();
                                MainFrame.this.tabbedPane.setSelectedIndex(0);
                            }
                        });
                    }
                });
            }
        }
                , false);
    }

    private WebScrollPane createComuneTab() {

        return new WebScrollPane(new WebPanel(new VerticalFlowLayout(5, 5)) {
            {
                add(new WebTable(new String[][]{
                        {"nom", "sample"},
                        {"region", "sample"},
                        {"departement", "sample"},
                        {"canton", "sample"},
                        {"code postal", "sample"},
                        {"altitude min", "sample"},
                        {"altitude max", "sample"},
                        {"latitude", "sample"},
                        {"longitude", "sample"},
                        {"superficie", "sample"},
                        {"population", "sample"},
                        {"densité", "sample"},

                }, new String[]{"", ""}) {
                    {
                        MainFrame.this.communeTable = this;
                        setEditable(false);
                        setTableHeader(null);
                    }
                });

                add(new WebButton("Recherche Region", new ImageIcon(resourceLoader.loadImage("search.png"))){
                    {
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.lastResult = new SearchProcessor.ResultSet(Arrays.asList(lastCommune.getRegion()), SearchProcessor.ResultSet.EMPTY_RESULT.departements, SearchProcessor.ResultSet.EMPTY_RESULT.communes);
                                updateSearchResultTree();
                                MainFrame.this.tabbedPane.setSelectedIndex(0);
                            }
                        });
                    }
                });

                add(new WebButton("Recherche Departement", new ImageIcon(resourceLoader.loadImage("search.png"))){
                    {
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.lastResult = new SearchProcessor.ResultSet(SearchProcessor.ResultSet.EMPTY_RESULT.regions, Arrays.asList(lastCommune.getDepartement()), SearchProcessor.ResultSet.EMPTY_RESULT.communes);
                                updateSearchResultTree();
                                MainFrame.this.tabbedPane.setSelectedIndex(0);
                            }
                        });
                    }
                });
            }
        }
                , false);
    }

    private WebTree createSearchResultTree() {
        final WebTree<MyTreeNode> tree = new WebTree<MyTreeNode>(rootNode);
        tree.setCellRenderer(new MyTreeCellRenderer(this.resourceLoader));
        //tree.setEditable(true);
        tree.setRootVisible(false);
        tree.setSelectionMode(WebTree.SINGLE_TREE_SELECTION);
        tree.setSelectionStyle(TreeSelectionStyle.group);
        tree.setAutoExpandSelectedNode(true);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                final MyTreeNode node = (MyTreeNode) tree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                final WrappedBaseClass userObject = node.getUserObject();
                if (userObject == null) {
                    return;
                }
                final BaseClass object = userObject.getContent();
                if (object == null) {
                    return;
                }
                MainFrame.this.changeRegion(object.getRegion());

                MainFrame.this.lastDepartement = null;
                MainFrame.this.lastCommune = null;
                clearCommunesPoints();

                if (object instanceof Commune) {
                    final Commune commune = (Commune) object;
                    MainFrame.this.lastCommune = commune;
                    MainFrame.this.lastDepartement = commune.getDepartement();

                    updateCommunePointHelper();
                    addPoint(commune);
                } else if (object instanceof Departement) {
                    MainFrame.this.lastDepartement = (Departement) object;
                }
                updateTables();
            }
        });
        return tree;
    }

    private WebMenuBar createMenuBar() {
        final WebMenuBar menuBar = new WebMenuBar();
        menuBar.setUndecorated(true);
        menuBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        menuBar.add(new WebMenu("Fichier") {
            {
                setMnemonic('F');
                add(new WebMenuItem("Options") {
                    {
                        setMnemonic('O');
                        setIcon(new ImageIcon(resourceLoader.loadImage("settings.png")));
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                new OptionsDialog(MainFrame.this).setVisible(true);
                            }
                        });
                    }
                });
                addSeparator();
                add(new WebMenuItem("Quitter") {
                    {
                        setIcon(new ImageIcon(resourceLoader.loadImage("exit.png")));
                        setMnemonic('Q');
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.setVisible(false);
                                MainFrame.this.dispose();
                            }
                        });
                    }
                });


            }
        });
        menuBar.add(new WebMenu("Rechercher") {
            {

                add(new WebMenuItem("Recherche Region") {
                    {
                        addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.searchbar.setText("Region | ");
                                MainFrame.this.searchbar.requestFocus();
                            }
                        });
                        setMnemonic('R');
                        setDisplayedMnemonicIndex(10);
                    }
                });
                add(new WebMenuItem("Recherche Departement") {
                    {
                        addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.searchbar.setText("Departement | ");
                                MainFrame.this.searchbar.requestFocus();
                            }
                        });
                        setMnemonic('D');
                        setDisplayedMnemonicIndex(10);
                    }
                });
                add(new WebMenuItem("Recherche Commune") {
                    {
                        addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.searchbar.setText("Commune | ");
                                MainFrame.this.searchbar.requestFocus();
                            }
                        });

                        setMnemonic('C');
                        setDisplayedMnemonicIndex(10);
                    }
                });
                addSeparator();

                add(new WebMenuItem("Recherche Commune Selon Population ...") {
                    {
                        addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                final AdvancedSearchDialog advancedSearchDialog = new AdvancedSearchDialog(MainFrame.this);
                                advancedSearchDialog.setVisible(true);
                                String text = advancedSearchDialog.getData();
                                if (text == null) {
                                    return;
                                }
                                int seuil;
                                try {
                                    seuil = Integer.parseInt(text);
                                } catch (NumberFormatException exception) {
                                    WebOptionPane.showMessageDialog(MainFrame.this, exception.getMessage(), "Error", WebOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                clearCommunesPoints();
                                updateCommunePointHelper();
                                MainFrame.this.lastResult = MainFrame.this.searchProcessor.search(getLastRegion(), seuil);
                                updateSearchResultTree();
                                for (Commune commune : lastResult.communes) {
                                    addPoint(commune);
                                }
                                // auto focus les resultats de recherches
                                MainFrame.this.tabbedPane.setSelectedIndex(0);
                                updateWindow();
                            }
                        });

                        setMnemonic('P');
                    }
                });

                setMnemonic('R'); //permet de faire alt + r pour acceder a ce menu

            }
        });

        if (DEBUG) {
            menuBar.add(new WebMemoryBar(), "END");
        }
        return menuBar;
    }

    private void addPoint(final Commune commune) {
        final CommunePointHelper.CommunePoint point = communePointHelper.createCommunePoint(commune);
        this.communePoints.add(point);
        imgcontener.add(point, 5, 3000);
        TooltipManager.setTooltip(point, commune.getNom());
        point.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainFrame.this.lastCommune = commune;
                MainFrame.this.lastDepartement = commune.getDepartement();
                updateTables();
                MainFrame.this.tabbedPane.setSelectedIndex(1);
            }
        });
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public SearchProcessor.ResultSet getLastResult() {
        return lastResult;
    }

    public Region getLastRegion() {
        return lastRegion;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public int getAutoExpendResultNbr() {
        return autoExpendResultNbr;
    }

    public void setAutoExpendResultNbr(int autoExpendResultNbr) {
        this.autoExpendResultNbr = autoExpendResultNbr;
    }

    public boolean isEnableAnimation() {
        return enableAnimation;
    }

    public void setEnableAnimation(boolean enableAnimation) {
        this.enableAnimation = enableAnimation;
    }
}
