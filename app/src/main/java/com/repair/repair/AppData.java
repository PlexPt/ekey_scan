package com.repair.repair;


import com.repair.repair.dto.AdminOp;
import com.repair.repair.dto.Order;
import com.repair.repair.dto.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 全局变量
 */
public class AppData {

    public boolean islogin = false;
    //当前用户
    public UserDTO user;
    //当前
    public Order order;

    public String server = "http://192.168.1.10:2300/";

    public String ip = "192.168.1.10";

    public int port = 2300;

    public Map<String, UserDTO> userDTOMap = new HashMap<>();

//    public Map<String, List<Order>> orderMap = new HashMap<>();

    public List<UserDTO> users = new ArrayList<>();

    public HashSet<Order> allorders = new HashSet<>();

    public List<Order> orders = new ArrayList<>();

    public ArrayList<AdminOp> adminOps = new ArrayList<>();


}
