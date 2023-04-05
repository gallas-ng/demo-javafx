package com.example.demojavafx;

import java.util.List;

public interface IStudent {
    public int add(Student s);
    public int update(Student s);
    public int delete(int id);
    public List<Student> list();
    public Student get(int id);
}
