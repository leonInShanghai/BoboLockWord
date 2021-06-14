package com.bobo.greendao;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by ���ںţ�IT�� on 2021/5/30 Copyright ? Leon. All rights reserved.
 * Functions: ���������һ�¾����� DaoGenerator ��صĶ����� ע��·������Ҫ
 */
public class MyClass {

    public static void main(String[] args) {
        // ����Schema����
        // ���췽����һ������Ϊ���ݿ�汾��
        // �ڶ�������Ϊ�Զ����ɵ�ʵ���ཫҪ��ŵ�λ��  src/main/java/com/bobo/bobolockword/MainActivity.java
        // Schema schema = new Schema(1000, "com.bobo.greendao.entity.greendao");
        Schema schema = new Schema(1000, "com.bobo.bobolockword.entity.greendao");
        // �����Ҫ������ʵ������Ϣ
        addNote(schema);
        try {
            // ����ʵ����.�ڶ���������Android Module��·��
            new DaoGenerator().generateAll(schema, "./app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ӽ�Ҫ������ʵ�������Ϣ,����������������ݿ�ı�,�������������ݿ���ֶ�<p>
     * ��Ȼ����뽨���ű�,���Դ������Entity����
     *
     * @param schema
     */
    private static void addNote(Schema schema) {
        // ָ����Ҫ����ʵ���������,����ȷ������ô����Ҳ�Ǹ�������������Զ�������,�����������,���ɵı�������WisdomEntity
        Entity entity = schema.addEntity("WisdomEntity");
        // ָ������������
        entity.addIdProperty().autoincrement().primaryKey();
        // ����������,���������������ݿ���е��ֶ�
        entity.addStringProperty("english");
        entity.addStringProperty("china");


        // ָ����Ҫ����ʵ���������,����ȷ������ô����Ҳ�Ǹ�������������Զ�������
        Entity entity1 = schema.addEntity("CET4Entity");
        // ָ������������
        entity1.addIdProperty().autoincrement().primaryKey();
        // ����������,���������������ݿ���е��ֶ�
        entity1.addStringProperty("word");
        entity1.addStringProperty("english");
        entity1.addStringProperty("china");
        entity1.addStringProperty("sign");
    }
}
