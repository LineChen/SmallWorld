package com.msg_relative;

public interface iMoMoMsgTypes {
	int REGISTER = 0;// ע��
	int REGISTER_SUCCESS = 1;// ע��ɹ�
	int REGISTER_FAILED = 2;// ע��ʧ��

	int LOGIN = 3;// ��¼
	int LOGIN_SUCCESS = 4;// ��¼�ɹ�
	int LOGIN_FAILED = 5;// ��¼ʧ��

	int LOGIN_SUPER_HEAD = 6;// ������¼,�û��ֻ����û�ͼ��,�����ȡͷ��
	int LOGIN_SUPER_NOHEAD = 7;//������¼,�û��ֻ����û�ͼ��,��Ҫ�ӷ�������ȡͷ�����Ϣ
	int LOGIN_SUPER_SUCCESS = 8;// �ɹ�
	int LOGIN_SUPER_FAILED = 9;// ʧ��

	int LOGOUT = 10;// �û�����

	int FIND_PASSWD = 11;// �һ�����
	int FIND_PASSWD_SUCCESS = 12;// �ѷ����һ������ʼ�
	int FIND_PASSWD_FAILED = 13;// �һ�����ʧ��

	int RESET_PASSWD = 14;// ��������
	int RESET_PASSWD_SUCCESS = 15;// ��������ɹ�
	int RESET_PASSWD_FAILED = 16;// ��������ʧ��

	int RESET_USERNAME = 17;// �޸��û���
//	int RESET_USERNAME_SUCCESS = 18;// �޸��û����ɹ�
//	int RESET_USERNAME_FAILED = 19;// �޸��û���ʧ��
	
	int RESET_SEX = 200;//�޸��Ա�
	int RESET_BIRTHDAY = 201;//�޸�����
	int RESET_SIGNATUE = 202;//�޸ĸ���ǩ��

	int RESET_HEAD = 20;// �޸�ͷ��
	int RESET_HEAD_SUCCESS = 21;// �޸�ͷ��ɹ�
	int RESET_HEAD_FAILED = 22;// �޸�ͷ��ʧ��

	int CHATING_TEXT_MSG = 23;// ������Ϣ-�ı���Ϣ
	int CHATING_VOICE_MSG = 24;// ������Ϣ-������Ϣ
	int CHATING_IMAGE_MSG = 25;// ������Ϣ-ͼƬ��Ϣ
	
	int CONNECT_DOWN = 26;//�ͷ����������ӶϿ�
	
	int LOCATION = 27;//�û��ĵ���λ��
	
	
	int GET_STRANGERS_LOC_ONEKM = 28;//����õ���Χһ����İ���˵���λ��
	int GET_STRANGERS_LOC_MORE =40; //����һ�������
	int STRANGERS_LIST_ONEKM = 31;//һ������İ�����б�
	int STRANGERS_LIST_MORE = 38;//���฽������
	int NO_STRANGERS = 32;//��Χ��İ����
	
	int GET_STRANGER_INFO = 50;//��ȡİ���˸�����Ϣ
	
	int ADD_FRIEND = 33;//��Ӻ���
	int ADD_FRIEND_SUCCESS = 36;//��ӳɹ�
	int ADD_FRIEND_FAILED = 37;//��ӳɹ�
	int DELETE_FRIEND = 35;//ɾ������
	
	int GET_FRIEND_ID_LIST = 34;//��ú���Id�б�
	int FRIEND_ID_LIST = 42;//����Id�б�
	int GETA_FRIEND_INFO_HEAD = 29;//�õ�һ��İ���˵ľ�����Ϣ(������ͼ��,���������÷���ͷ��)
	int GETA_FRIEND_INFO_NOHEAD = 30;//�õ�һ��İ���˵ľ�����Ϣ(������ͼ��,������Ҫ��ͷ��)
	
	
	int REBACK = 51;//�û�����
	
	
	int GROUP_INVITE = 52;//Ⱥ��������
	
	int SIGN = 55;//ÿ��ǩ��
	
	int CREATE_GROUP = 56;//����Ⱥ��
	int CREATE_GROUP_SUCCESS = 57;
	int CREATE_GROUP_FAILED = 58;
	
	int INVITE_TO_GROUP = 59;//��������������
	
	int AGREEE_TO_GROUP = 60;//ͬ�����Ⱥ��
	int GROUP_MSG = 61;
	
	
	int PASS_GAME = 62;//����ֵͨ��
}



