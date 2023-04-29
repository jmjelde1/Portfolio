#!/usr/bin/env python3



import mysql.connector
import os.path
from db_tunnel import DatabaseTunnel

DB_HOST = "cs.westminstercollege.edu"
DB_SSH_PORT = 2322
DB_SSH_USER = "student"
DB_PORT = 3306

# Default connection information (can be overridden with command-line arguments)
DB_SSH_KEYFILE = "id_rsa.cmpt307"
DB_NAME = "tmm0604_SkiTeamInventory_final"
DB_USER = "token_b19c"
DB_PASSWORD = "QR36wtF7meDuoigp"

# The queries that will be executed
QUERY1 = """
Select  Person.name, Person.age, Person.sex, Brand.name as Brand, count(*) numberOfPairs
From Person
         JOIN Brand on Brand.id = Person.brandId
         join Ski on Person.id = Ski.personId
group by Person.id
"""

QUERY2 = """
Select count(Person.name)
From Person
           JOIN Brand on Brand.id = Person.brandId
Where Brand.name = %s
"""

QUERY3 = """
Select Person.name, Brand.name
From Person
         JOIN Brand on Brand.id = Person.brandId
         JOIN Country on Country.id = Person.countryId
Where Country.country = %s
"""

QUERY4 = """
select Person.name, Brand.name, Ski.discipline, Ski.pairNum
from Person
 join Ski on Ski.personId = Person.id
 join Brand on Ski.brandId = Brand.id
where Person.name = %s
"""

QUERY5 = """
select Person.name, count(*) as count
FROM Person
	JOIN Ski ON Person.id = Ski.personId
group by Person.id
having count > %s
"""

QUERY6 = """
Delete from Person where name = %s
"""




class skiTeam:


    def __init__(self, dbHost, dbPort, dbName, dbUser, dbPassword):

        self.dbHost, self.dbPort = dbHost, dbPort
        self.dbName = dbName
        self.dbUser, self.dbPassword = dbUser, dbPassword

    def __enter__(self):
        self.connect()
        return self

    def __exit__(self, *args):
        self.close()

    def connect(self):
        self.connection = mysql.connector.connect(
                host=self.dbHost, port=self.dbPort, database=self.dbName,
                user=self.dbUser, password=self.dbPassword,
                use_pure=True
        )
        self.cursor = self.connection.cursor()

    def close(self):
        self.connection.close()

    def runApp(self):
        line = input("\nChoose one of the following keywords: description, display, brand, nation, person, pairs, event, delete quit\n"
                     "\ndescription - Displays the keywords and their descrptions\n"
                     "display - Displays name, age, sex, Ski brand, and number of pairs for everyone on team\n"
                     "brand - Outputs the amount of athletes that use the specific brand\n"
                     "nation - Displays all athletes from that nation and their brands\n"
                     "person - Prints a list of all the skis of belonging to that athlete and info about each pair\n"
                     "pairs - Outputs a list of athletes that use more than the inputted amount of pairs\n"
                     "event - Outputs amount of skis in inputted discipline\n"
                     "delete - Deletes the inputted person from the table\n")
        line = line.lower()

        while line != "quit":
            if line == "display":
                self.cursor.execute(QUERY1)
                count = 0
                for (name, age, sex, brand, numPairs) in self.cursor:
                    count+=1
                    print(f"{name} {age} {sex} {brand} {numPairs}")

                print(f"\n{count} athletes on roster")
                line = input("Choose one of the following keywords: description, display, brand, nation, person, pairs, event, delete, quit\n")
                line = line.lower()

            elif line == "brand":
                brand =input("Which brand?")
                self.cursor.execute(QUERY2,(brand,))
                result = self.cursor.fetchone()
                print(result[0], "athlete(s) use", brand, "as their brand")

                line = input("Choose one of the following keywords: description, display, brand, nation, person, pairs, event, delete, quit\n")
                line = line.lower()

            elif line == "nation":
                nation = input("Which nation?")
                self.cursor.execute(QUERY3, (nation,))
                for (name, brand) in self.cursor:

                    print(f"{name} {brand} ")
                line = input("Choose one of the following keywords: description, display, brand, nation, person, pairs, event, delete, quit\n")
                line = line.lower()

            elif line == "person":
                person = input("Who?")
                self.cursor.execute(QUERY4, (person,))
                for (name, brand, discipline, pair) in self.cursor:
                    print(f"{name} {brand} {discipline} {pair}")
                line = input("Choose one of the following keywords: description, display, brand, nation, person, pairs, event, delete, quit\n")
                line = line.lower()


            elif line == "pairs":
                pairs = input("How many pairs?")
                self.cursor.execute(QUERY5, (pairs,))

                for (name, num) in self.cursor:
                    print(f"{name} {num} ")
                line = input("Choose one of the following keywords: description, display, brand, nation, person, pairs, event, delete, quit\n")
                line = line.lower()

            elif line == "delete":
                delPer = input("Which person do you want to delete from the database?")
                self.cursor.execute(QUERY6, (delPer,))
                self.connection.commit()
                print(delPer, "has successfully been deleted from the database\n")
                line = input("Choose one of the following keywords: description, display, brand, nation, person, pairs, event, delete, quit\n")
                line = line.lower()


            elif line == "event":
                dis = input("enter either \"SL\" for Slalom or \"GS\" for Giant Slalom")
                dis = dis.upper()

                while(dis != "SL" and dis != "GS"):
                    dis = input("enter either \"SL\" for Slalom or \"GS\" for Giant Slalom")
                    dis = dis.upper()

                if dis == "SL" or dis == "GS":
                    params = (dis, None)
                    params = self.cursor.callproc("TotalSkis", params)

                    print(params[1], "pairs of skis in", dis)


                line = input("Choose one of the following keywords: description, display, brand, nation, person, pairs, event, delete, quit\n")
                line = line.lower()

            elif line=="description":
                line = input(
                    "\nChoose one of the following keywords: display, brand, nation, person, pairs, event, delete quit\n"
                    "\ndescription - Displays the keywords and their descrptions\n"
                    "display - Displays name, age, sex, Ski brand, and number of pairs for everyone on team\n"
                    "brand - Outputs the amount of athletes that use the specific brand\n"
                    "nation - Displays all athletes from that nation and their brands\n"
                    "person - Prints a list of all the skis of belonging to that athlete and info about each pair\n"
                    "pairs - Outputs a list of athletes that use more than the inputted amount of pairs\n"
                    "event - Outputs amount of skis in inputted discipline\n"
                    "delete - Deletes the inputted person from the table\n")
                line = line.lower()
            else:
                line = input("Choose one of the following keywords: description, display, brand, nation, person, pairs, event, delete, quit\n")
                line = line.lower()



def main():
    import sys
    '''Entry point of the application. Uses command-line parameters to override database connection settings, then invokes runApp().'''
    # Default connection parameters (can be overridden on command line)
    params = {
        'sshkeyfile':   DB_SSH_KEYFILE,
        'dbname':       DB_NAME,
        'user':         DB_USER,
        'password':     DB_PASSWORD
    }

    needToPrintHelp = False

    # Parse command-line arguments, overriding values in params
    i = 1
    while i < len(sys.argv) and not needToPrintHelp:
        arg = sys.argv[i]
        isLast = (i + 1 == len(sys.argv))

        if arg in ("-h", "-help"):
            needToPrintHelp = True
            break

        elif arg in ("-sshkeyfile", "-dbname", "-user", "-password"):
            if isLast:
                needToPrintHelp = True
            else:
                params[arg[1:]] = sys.argv[i + 1]
                i += 1
            break

        else:
            print("Unrecognized option: " + arg, file=sys.stderr)
            needToPrintHelp = True

        i += 1

    # If help was requested, print it and exit
    if needToPrintHelp:
        printHelp()
        return

    if not os.path.isfile(params['sshkeyfile']):
        print(f'''SSH key file does not exist. Either use -sshkeyfile to specify the
correct filename/location or move the keyfile to the location below.
\tI expected to find the SSH key file at: {params['sshkeyfile']}\n\n''', file=sys.stderr)
        sys.exit(1);

    try:
        with \
            DatabaseTunnel(params['sshkeyfile']) as tunnel, \
            skiTeam(
                dbHost='localhost', dbPort=tunnel.getForwardedPort(),
                dbName=params['dbname'],
                dbUser=params['user'], dbPassword=params['password']
            ) as app:
            app.runApp()
    except mysql.connector.Error as err:
        print("Error communicating with the database (see full message below).", file=sys.stderr)
        print(err, file=sys.stderr)
        print("\nParameters used to connect to the database:", file=sys.stderr)
        print(f"\tSSH keyfile: {params['sshkeyfile']}\n\tDatabase name: {params['dbname']}\n\tUser: {params['user']}\n\tPassword: {params['password']}", file=sys.stderr)
        print("""
(Did you install mysql-connector-python and sshtunnel with pip3/pip?)
(Are the username and password correct?)""", file=sys.stderr)


def printHelp():
    print(f'''
Accepted command-line arguments:
    -help, -h          display this help text
    -sshkeyfile <path> override ssh keyfile
                       (default: {DB_SSH_KEYFILE})
    -dbname <text>     override name of database to connect to
                       (default: {DB_NAME})
    -user <text>       override database user
                       (default: {DB_USER})
    -password <text>   override database password
    ''')


if __name__ == "__main__":
    main()
