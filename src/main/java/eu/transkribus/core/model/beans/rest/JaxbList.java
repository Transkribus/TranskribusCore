package eu.transkribus.core.model.beans.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.util.GsonUtil;
import eu.transkribus.core.util.JaxbUtils;

@XmlRootElement(name="List")
public class JaxbList<T>{
	
    protected List<T> list;

    public JaxbList(){
    	this.list = new ArrayList<T>();
    }

    public JaxbList(List<T> list){
    	this.list=list;
    }

    @XmlElement(name="Item")
    public List<T> getList(){
    	return list;
    }
    
    public void setList(List<T> list){
    	this.list=list;
    }
    
    public void add(T e) {
    	this.list.add(e);
    }
    
    public void remove(T o) {
    	this.list.remove(o);
    }
    
    public static void main(String[] args) throws Exception {
    	List<Integer> l = new ArrayList<>();
		l.add(1);
		l.add(3);
		l.add(6);
    	
		JaxbList<Integer> intList = new JaxbList<>(l);
		System.out.println(JaxbUtils.marshalToString(intList));
		System.out.println(GsonUtil.toJson(intList));
		System.out.println(GsonUtil.toJson(l));
		
	}
}