import psycopg2 as pg


def connection_check():
    conn = None
    try:
        conn = pg.connect(host="localhost", database="COMPANY", user="postgres", password="postgres")
        print('PostgreSQL database version:')
        cur = conn.cursor()
        cur.execute('SELECT version()')
        db_version = cur.fetchone()
        print(db_version)
        cur.close()

    except (Exception, pg.DatabaseError) as error:
        print(error)

    finally:
        if conn is not None:
            conn.close()
            print('Database connection closed.')


def insert_employee(values):
    sql = "INSERT INTO employee VALUES(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
    conn = None
    try:
        conn = pg.connect(host="localhost", database="COMPANY", user="postgres", password="postgres")
        cur = conn.cursor()
        cur.executemany(sql, values)
        conn.commit()
        # close communication with the database
        cur.close()
    except (Exception, pg.DatabaseError) as error:
        print(error)
    finally:
        if conn is not None:
            conn.close()


insert_employee([("javad", "M", "abdi", "123231312", "1998-01-07", "tehran-tarasht-1234", "M", "10000", "123456789", "1"),
                 (
                 "javad", "M", "abdi", "231312123", "1998-01-07", "tehran-tarasht-1234", "M", "10000", "123456789", "1")])

