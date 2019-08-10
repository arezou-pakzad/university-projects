import psycopg2 as pg
userName = "postgres"
passWord = "snowboard"


def connect():
    conn = None
    try:
        conn = pg.connect(host="localhost", database="DB_Project", user=userName, password=passWord)
    except(Exception, pg.DatabaseError) as error:
        print(error)
    finally:
        if conn is not None:
            return conn


def close_connection(connection):
    connection.close()


def execute(sql, values):
    conn = None
    try:
        conn = pg.connect(host="localhost", database="DB_Project", user=userName, password=passWord)
        cur = conn.cursor()
        cur.execute(sql, values)
        conn.commit()
        cur.close()
    except (Exception, pg.DatabaseError) as error:
        print(error)
        # todo: print eror ro bardaram
        return False
    finally:
        if conn is not None:
            conn.close()
    return True


def execute_many(sql, values):
    conn = None
    try:
        conn = pg.connect(host="localhost", database="DB_Project", user=userName, password=passWord)
        cur = conn.cursor()
        cur.executemany(sql, values)
        conn.commit()
        cur.close()
    except (Exception, pg.DatabaseError) as error:
        print(error)
    finally:
        if conn is not None:
            conn.close()


def execute_fetch(sql, values):
    conn = None
    out = None
    try:
        conn = pg.connect(host="localhost", database="DB_Project", user=userName, password=passWord)
        cur = conn.cursor()
        cur.execute(sql, values)
        out = cur.fetchall()
        conn.commit()
        cur.close()
    except (Exception, pg.DatabaseError) as error:
        print(error)
    finally:
        if conn is not None:
            conn.close()
        return out


def execute_many_fetch(sql, values):
    conn = None
    out = None
    try:
        conn = pg.connect(host="localhost", database="DB_Project", user=userName, password=passWord)
        cur = conn.cursor()
        cur.executemany(sql, values)
        out = cur.fetchall()
        conn.commit()
        cur.close()
    except (Exception, pg.DatabaseError) as error:
        print(error)
    finally:
        if conn is not None:
            conn.close()
        return out

