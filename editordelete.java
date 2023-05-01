private static class BuyerGUI extends JFrame {
    private JButton marketPlaceButton;
    private JButton shopBySellerButton;
    private JButton editAccountButton;
    private JButton deleteAccountButton;
    private User user;

    public BuyerGUI(User user) {
        super("Would you like to view the whole marketplace or shop by seller?");
        marketPlaceButton = new JButton("View the whole marketplace");
        shopBySellerButton = new JButton("Shop by seller");
        editAccountButton = new JButton("Edit Account");
        deleteAccountButton = new JButton("Delete Account");
        this.user = user;

        JPanel panel = new JPanel();
        panel.setSize(600,400);
        panel.add(marketPlaceButton);
        panel.add(shopBySellerButton);
        panel.add(editAccountButton);
        panel.add(deleteAccountButton);
        add(panel);

        marketPlaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // asking for balance before creating buyer object
                String bal = JOptionPane.showInputDialog(null, "What is your budget?",
                        "Budget Information", JOptionPane.QUESTION_MESSAGE);

                double balance = Double.parseDouble(bal);

                // creating buyer object from user data that was generated in the login frame
                Buyer buyer = new Buyer(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                        user.getAge(), balance);


                // maybe send prompt to server for the database and then read the database in:
//                    dos.writeUTF();
//                    dis.readUTF();

                // generate new marketplace JFrame here to show what is in the marketplace
                Client.marketPlace marketPlace = new Client.marketPlace(buyer);
                marketPlace.setVisible(true);
                dispose();


            }
        });

        shopBySellerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // asking for balance before creating buyer object
                String bal = JOptionPane.showInputDialog(null, "What is your budget?",
                        "Budget Information", JOptionPane.QUESTION_MESSAGE);

                double balance = Double.parseDouble(bal);

                // creating buyer object from user data that was generated in the login frame
                Buyer buyer = new Buyer(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                        user.getAge(), balance);

                // maybe send prompt to server for the database and then read the database in:
//                    dos.writeUTF();
//                    dis.readUTF();

                // generate new shop by seller Jframe here to allow the buyer to search for a seller

            }
        });
        editAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
                currentFrame.dispose(); // Dispose the current JFrame

                editAccount editAccount = new editAccount(user);
                JFrame editAccountFrame = new JFrame("Edit Account");
                editAccountFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                editAccountFrame.getContentPane().add(editAccount);
                editAccountFrame.pack();
                editAccountFrame.setLocationRelativeTo(null);
                editAccountFrame.setVisible(true);
            }
        });
        deleteAccountButton.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int reply = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete your account?", "Delete account",
                        JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    Window window = SwingUtilities.windowForComponent(deleteAccountButton);
                    window.dispose();
                    // TODO server for delete account
                } else if (reply == JOptionPane.NO_OPTION) {
                    // Leave as is
                }
            }
        }));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

    }
