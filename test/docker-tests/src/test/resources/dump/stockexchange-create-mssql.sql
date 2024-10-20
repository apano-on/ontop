﻿USE [stockexchange_new]
GO
ALTER TABLE [dbo].[transactions] DROP CONSTRAINT [fk_stockinformation_pkey]
GO
ALTER TABLE [dbo].[transactions] DROP CONSTRAINT [fk_forcompanyid_pkey]
GO
ALTER TABLE [dbo].[transactions] DROP CONSTRAINT [fk_forclientid_pkey]
GO
ALTER TABLE [dbo].[transactions] DROP CONSTRAINT [fk_broker_transaction_pkey]
GO
ALTER TABLE [dbo].[stockinformation] DROP CONSTRAINT [fk_company_pkey]
GO
ALTER TABLE [dbo].[stockbooklist] DROP CONSTRAINT [fk_stockid_pkey]
GO
ALTER TABLE [dbo].[person] DROP CONSTRAINT [fk_address_pkey]
GO
ALTER TABLE [dbo].[company] DROP CONSTRAINT [fk_address_company_pkey]
GO
ALTER TABLE [dbo].[client] DROP CONSTRAINT [fk_person_client_pkey]
GO
ALTER TABLE [dbo].[brokerworksfor] DROP CONSTRAINT [fk_brokerworksfor_companyid_pkey]
GO
ALTER TABLE [dbo].[brokerworksfor] DROP CONSTRAINT [fk_brokerworksfor_clientid_pkey]
GO
ALTER TABLE [dbo].[brokerworksfor] DROP CONSTRAINT [fk_brokerworksfor_brokerid_pkey]
GO
ALTER TABLE [dbo].[brokerworksfor] DROP CONSTRAINT [fk_broker_pkey]
GO
ALTER TABLE [dbo].[broker] DROP CONSTRAINT [fk_person_broker_pkey]
GO
/****** Object:  Table [dbo].[transactions]    Script Date: 19.01.17 14:47:03 ******/
DROP TABLE [dbo].[transactions]
GO
/****** Object:  Table [dbo].[stockinformation]    Script Date: 19.01.17 14:47:03 ******/
DROP TABLE [dbo].[stockinformation]
GO
/****** Object:  Table [dbo].[stockbooklist]    Script Date: 19.01.17 14:47:03 ******/
DROP TABLE [dbo].[stockbooklist]
GO
/****** Object:  Table [dbo].[person]    Script Date: 19.01.17 14:47:03 ******/
DROP TABLE [dbo].[person]
GO
/****** Object:  Table [dbo].[company]    Script Date: 19.01.17 14:47:03 ******/
DROP TABLE [dbo].[company]
GO
/****** Object:  Table [dbo].[client]    Script Date: 19.01.17 14:47:03 ******/
DROP TABLE [dbo].[client]
GO
/****** Object:  Table [dbo].[brokerworksfor]    Script Date: 19.01.17 14:47:03 ******/
DROP TABLE [dbo].[brokerworksfor]
GO
/****** Object:  Table [dbo].[broker]    Script Date: 19.01.17 14:47:03 ******/
DROP TABLE [dbo].[broker]
GO
/****** Object:  Table [dbo].[address]    Script Date: 19.01.17 14:47:03 ******/
DROP TABLE [dbo].[address]
GO
USE [master]
GO
/****** Object:  Database [stockexchange_new]    Script Date: 19.01.17 14:47:03 ******/
DROP DATABASE [stockexchange_new]
GO
/****** Object:  Database [stockexchange_new]    Script Date: 19.01.17 14:47:03 ******/
CREATE DATABASE [stockexchange_new]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'stockexchange_new', FILENAME = N'S:\Program Files\Microsoft SQL Server\MSSQL11.MSSQLSERVER\MSSQL\DATA\stockexchange_new.mdf' , SIZE = 4160KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'stockexchange_new_log', FILENAME = N'S:\Program Files\Microsoft SQL Server\MSSQL11.MSSQLSERVER\MSSQL\DATA\stockexchange_new_log.ldf' , SIZE = 1344KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [stockexchange_new] SET COMPATIBILITY_LEVEL = 110
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [stockexchange_new].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [stockexchange_new] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [stockexchange_new] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [stockexchange_new] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [stockexchange_new] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [stockexchange_new] SET ARITHABORT OFF 
GO
ALTER DATABASE [stockexchange_new] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [stockexchange_new] SET AUTO_CREATE_STATISTICS ON 
GO
ALTER DATABASE [stockexchange_new] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [stockexchange_new] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [stockexchange_new] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [stockexchange_new] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [stockexchange_new] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [stockexchange_new] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [stockexchange_new] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [stockexchange_new] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [stockexchange_new] SET  ENABLE_BROKER 
GO
ALTER DATABASE [stockexchange_new] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [stockexchange_new] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [stockexchange_new] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [stockexchange_new] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [stockexchange_new] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [stockexchange_new] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [stockexchange_new] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [stockexchange_new] SET RECOVERY FULL 
GO
ALTER DATABASE [stockexchange_new] SET  MULTI_USER 
GO
ALTER DATABASE [stockexchange_new] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [stockexchange_new] SET DB_CHAINING OFF 
GO
ALTER DATABASE [stockexchange_new] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [stockexchange_new] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
EXEC sys.sp_db_vardecimal_storage_format N'stockexchange_new', N'ON'
GO
USE [stockexchange_new]
GO
/****** Object:  Table [dbo].[address]    Script Date: 19.01.17 14:47:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[address](
	[id] [int] NOT NULL,
	[street] [varchar](100) NOT NULL,
	[number] [int] NOT NULL,
	[city] [varchar](100) NOT NULL,
	[state] [varchar](100) NOT NULL,
	[country] [varchar](100) NOT NULL,
 CONSTRAINT [address_pkey] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[broker]    Script Date: 19.01.17 14:47:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[broker](
	[id] [int] NOT NULL,
 CONSTRAINT [broker_pkey] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[brokerworksfor]    Script Date: 19.01.17 14:47:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[brokerworksfor](
	[brokerid] [int] NOT NULL,
	[companyid] [int] NULL,
	[clientid] [int] NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[client]    Script Date: 19.01.17 14:47:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[client](
	[id] [int] NOT NULL,
 CONSTRAINT [client_pkey] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[company]    Script Date: 19.01.17 14:47:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[company](
	[id] [int] NOT NULL,
	[name] [varchar](100) NOT NULL,
	[marketshares] [int] NOT NULL,
	[networth] [float] NOT NULL,
	[addressid] [int] NOT NULL,
 CONSTRAINT [company_pkey] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[person]    Script Date: 19.01.17 14:47:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[person](
	[id] [int] NOT NULL,
	[name] [varchar](100) NOT NULL,
	[lastname] [varchar](100) NOT NULL,
	[dateofbirth] [date] NOT NULL,
	[ssn] [varchar](100) NOT NULL,
	[addressid] [int] NOT NULL,
 CONSTRAINT [person_pkey] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[stockbooklist]    Script Date: 19.01.17 14:47:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[stockbooklist](
	[date] [date] NOT NULL,
	[stockid] [int] NOT NULL,
 CONSTRAINT [stockbooklist_pkey] PRIMARY KEY CLUSTERED 
(
	[date] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[stockinformation]    Script Date: 19.01.17 14:47:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[stockinformation](
	[id] [int] NOT NULL,
	[numberofshares] [int] NOT NULL,
	[sharetype] [bit] NOT NULL,
	[companyid] [int] NOT NULL,
	[description] [varchar](200) NOT NULL,
 CONSTRAINT [stockinformation_pkey] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[transactions]    Script Date: 19.01.17 14:47:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[transactions](
	[id] [int] NOT NULL,
	[date] [datetime] NOT NULL,
	[stockid] [int] NOT NULL,
	[type] [bit] NOT NULL,
	[brokerid] [int] NOT NULL,
	[forclientid] [int] NULL,
	[forcompanyid] [int] NULL,
	[amount] [decimal](18, 4) NOT NULL,
 CONSTRAINT [transaction_pkey] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
INSERT [dbo].[address] ([id], [street], [number], [city], [state], [country]) VALUES (991, N'Road street', 24, N'Chonala', N'Veracruz', N'Mexico')
INSERT [dbo].[address] ([id], [street], [number], [city], [state], [country]) VALUES (992, N'Via Marconi', 3, N'Bolzano', N'Bolzano', N'Italy')
INSERT [dbo].[address] ([id], [street], [number], [city], [state], [country]) VALUES (993, N'Romer Street', 32, N'Malaga', N'Malaga', N'Spain')
INSERT [dbo].[address] ([id], [street], [number], [city], [state], [country]) VALUES (995, N'Huberg Strasse', 3, N'Bolzano', N'Bolzano', N'Italy')
INSERT [dbo].[address] ([id], [street], [number], [city], [state], [country]) VALUES (996, N'Via Piani di Bolzano', 7, N'Marconi', N'Trentino', N'Italy')
INSERT [dbo].[address] ([id], [street], [number], [city], [state], [country]) VALUES (997, N'Samara road', 9976, N'Puebla', N'Puebla', N'Mexico')
INSERT [dbo].[address] ([id], [street], [number], [city], [state], [country]) VALUES (998, N'Jalan Madura 12', 245, N'Jakarta', N'Jakarta', N'Indonesia')
INSERT [dbo].[broker] ([id]) VALUES (112)
INSERT [dbo].[broker] ([id]) VALUES (113)
INSERT [dbo].[broker] ([id]) VALUES (114)
INSERT [dbo].[brokerworksfor] ([brokerid], [companyid], [clientid]) VALUES (112, NULL, 111)
INSERT [dbo].[brokerworksfor] ([brokerid], [companyid], [clientid]) VALUES (112, NULL, 112)
INSERT [dbo].[brokerworksfor] ([brokerid], [companyid], [clientid]) VALUES (113, 212, NULL)
INSERT [dbo].[brokerworksfor] ([brokerid], [companyid], [clientid]) VALUES (113, 211, NULL)
INSERT [dbo].[brokerworksfor] ([brokerid], [companyid], [clientid]) VALUES (114, 212, NULL)
INSERT [dbo].[brokerworksfor] ([brokerid], [companyid], [clientid]) VALUES (114, NULL, 111)
INSERT [dbo].[brokerworksfor] ([brokerid], [companyid], [clientid]) VALUES (112, NULL, 111)
INSERT [dbo].[brokerworksfor] ([brokerid], [companyid], [clientid]) VALUES (112, NULL, 112)
INSERT [dbo].[brokerworksfor] ([brokerid], [companyid], [clientid]) VALUES (113, 212, NULL)
INSERT [dbo].[brokerworksfor] ([brokerid], [companyid], [clientid]) VALUES (113, 211, NULL)
INSERT [dbo].[brokerworksfor] ([brokerid], [companyid], [clientid]) VALUES (114, 212, NULL)
INSERT [dbo].[brokerworksfor] ([brokerid], [companyid], [clientid]) VALUES (114, NULL, 111)
INSERT [dbo].[client] ([id]) VALUES (111)
INSERT [dbo].[client] ([id]) VALUES (112)
INSERT [dbo].[company] ([id], [name], [marketshares], [networth], [addressid]) VALUES (211, N'General Motors', 25000000, 1234.5678, 995)
INSERT [dbo].[company] ([id], [name], [marketshares], [networth], [addressid]) VALUES (212, N'GnA Investments', 100000, 1234.5678, 996)
INSERT [dbo].[person] ([id], [name], [lastname], [dateofbirth], [ssn], [addressid]) VALUES (111, N'John', N'Smith', CAST(0x00DD0A00 AS Date), N'JSRX229500321', 991)
INSERT [dbo].[person] ([id], [name], [lastname], [dateofbirth], [ssn], [addressid]) VALUES (112, N'Joana', N'Lopatenkko', CAST(0xFCF90A00 AS Date), N'JLPTK54992', 992)
INSERT [dbo].[person] ([id], [name], [lastname], [dateofbirth], [ssn], [addressid]) VALUES (113, N'Walter', N'Schmidt', CAST(0x55F70A00 AS Date), N'WSCH9820783903', 993)
INSERT [dbo].[person] ([id], [name], [lastname], [dateofbirth], [ssn], [addressid]) VALUES (114, N'Patricia', N'Lombrardi', CAST(0x90000B00 AS Date), N'PTLM8878767830', 997)
INSERT [dbo].[stockbooklist] ([date], [stockid]) VALUES (CAST(0xCC2F0B00 AS Date), 661)
INSERT [dbo].[stockbooklist] ([date], [stockid]) VALUES (CAST(0xCD2F0B00 AS Date), 662)
INSERT [dbo].[stockbooklist] ([date], [stockid]) VALUES (CAST(0xCE2F0B00 AS Date), 663)
INSERT [dbo].[stockbooklist] ([date], [stockid]) VALUES (CAST(0xCF2F0B00 AS Date), 664)
INSERT [dbo].[stockbooklist] ([date], [stockid]) VALUES (CAST(0xD02F0B00 AS Date), 665)
INSERT [dbo].[stockbooklist] ([date], [stockid]) VALUES (CAST(0xD12F0B00 AS Date), 666)
INSERT [dbo].[stockbooklist] ([date], [stockid]) VALUES (CAST(0xD22F0B00 AS Date), 667)
INSERT [dbo].[stockbooklist] ([date], [stockid]) VALUES (CAST(0xD32F0B00 AS Date), 668)
INSERT [dbo].[stockbooklist] ([date], [stockid]) VALUES (CAST(0xD42F0B00 AS Date), 669)
INSERT [dbo].[stockinformation] ([id], [numberofshares], [sharetype], [companyid], [description]) VALUES (660, 100, 0, 211, N'Text description 2')
INSERT [dbo].[stockinformation] ([id], [numberofshares], [sharetype], [companyid], [description]) VALUES (661, 100, 0, 211, N'Text description 1')
INSERT [dbo].[stockinformation] ([id], [numberofshares], [sharetype], [companyid], [description]) VALUES (662, 100, 0, 211, N'Text description 3')
INSERT [dbo].[stockinformation] ([id], [numberofshares], [sharetype], [companyid], [description]) VALUES (663, 100, 0, 211, N'Text description 4')
INSERT [dbo].[stockinformation] ([id], [numberofshares], [sharetype], [companyid], [description]) VALUES (664, 100, 0, 211, N'Text description 5')
INSERT [dbo].[stockinformation] ([id], [numberofshares], [sharetype], [companyid], [description]) VALUES (665, 100, 1, 211, N'Testo di descrizione 1')
INSERT [dbo].[stockinformation] ([id], [numberofshares], [sharetype], [companyid], [description]) VALUES (666, 100, 1, 211, N'Testo di descrizione 2')
INSERT [dbo].[stockinformation] ([id], [numberofshares], [sharetype], [companyid], [description]) VALUES (667, 100, 1, 211, N'Testo di descrizione 3')
INSERT [dbo].[stockinformation] ([id], [numberofshares], [sharetype], [companyid], [description]) VALUES (668, 100, 1, 211, N'Testo di descrizione 5')
INSERT [dbo].[stockinformation] ([id], [numberofshares], [sharetype], [companyid], [description]) VALUES (669, 100, 1, 211, N'Testo di descrizione 4')
INSERT [dbo].[transactions] ([id], [date], [stockid], [type], [brokerid], [forclientid], [forcompanyid], [amount]) VALUES (3331, CAST(0x00009A7100000000 AS DateTime), 661, 1, 112, 111, NULL, CAST(12.6000 AS Decimal(18, 4)))
INSERT [dbo].[transactions] ([id], [date], [stockid], [type], [brokerid], [forclientid], [forcompanyid], [amount]) VALUES (3332, CAST(0x00009A7200000000 AS DateTime), 662, 1, 112, 111, NULL, CAST(108.3400 AS Decimal(18, 4)))
INSERT [dbo].[transactions] ([id], [date], [stockid], [type], [brokerid], [forclientid], [forcompanyid], [amount]) VALUES (3333, CAST(0x00009A7300000000 AS DateTime), 663, 1, 112, NULL, 212, CAST(-2.3490 AS Decimal(18, 4)))
INSERT [dbo].[transactions] ([id], [date], [stockid], [type], [brokerid], [forclientid], [forcompanyid], [amount]) VALUES (3334, CAST(0x00009A7E00000000 AS DateTime), 663, 1, 113, NULL, NULL, CAST(1667.0092 AS Decimal(18, 4)))
ALTER TABLE [dbo].[broker]  WITH CHECK ADD  CONSTRAINT [fk_person_broker_pkey] FOREIGN KEY([id])
REFERENCES [dbo].[person] ([id])
GO
ALTER TABLE [dbo].[broker] CHECK CONSTRAINT [fk_person_broker_pkey]
GO
ALTER TABLE [dbo].[brokerworksfor]  WITH CHECK ADD  CONSTRAINT [fk_broker_pkey] FOREIGN KEY([brokerid])
REFERENCES [dbo].[broker] ([id])
GO
ALTER TABLE [dbo].[brokerworksfor] CHECK CONSTRAINT [fk_broker_pkey]
GO
ALTER TABLE [dbo].[brokerworksfor]  WITH CHECK ADD  CONSTRAINT [fk_brokerworksfor_brokerid_pkey] FOREIGN KEY([brokerid])
REFERENCES [dbo].[broker] ([id])
GO
ALTER TABLE [dbo].[brokerworksfor] CHECK CONSTRAINT [fk_brokerworksfor_brokerid_pkey]
GO
ALTER TABLE [dbo].[brokerworksfor]  WITH CHECK ADD  CONSTRAINT [fk_brokerworksfor_clientid_pkey] FOREIGN KEY([clientid])
REFERENCES [dbo].[client] ([id])
GO
ALTER TABLE [dbo].[brokerworksfor] CHECK CONSTRAINT [fk_brokerworksfor_clientid_pkey]
GO
ALTER TABLE [dbo].[brokerworksfor]  WITH CHECK ADD  CONSTRAINT [fk_brokerworksfor_companyid_pkey] FOREIGN KEY([companyid])
REFERENCES [dbo].[company] ([id])
GO
ALTER TABLE [dbo].[brokerworksfor] CHECK CONSTRAINT [fk_brokerworksfor_companyid_pkey]
GO
ALTER TABLE [dbo].[client]  WITH CHECK ADD  CONSTRAINT [fk_person_client_pkey] FOREIGN KEY([id])
REFERENCES [dbo].[person] ([id])
GO
ALTER TABLE [dbo].[client] CHECK CONSTRAINT [fk_person_client_pkey]
GO
ALTER TABLE [dbo].[company]  WITH CHECK ADD  CONSTRAINT [fk_address_company_pkey] FOREIGN KEY([addressid])
REFERENCES [dbo].[address] ([id])
GO
ALTER TABLE [dbo].[company] CHECK CONSTRAINT [fk_address_company_pkey]
GO
ALTER TABLE [dbo].[person]  WITH CHECK ADD  CONSTRAINT [fk_address_pkey] FOREIGN KEY([addressid])
REFERENCES [dbo].[address] ([id])
GO
ALTER TABLE [dbo].[person] CHECK CONSTRAINT [fk_address_pkey]
GO
ALTER TABLE [dbo].[stockbooklist]  WITH CHECK ADD  CONSTRAINT [fk_stockid_pkey] FOREIGN KEY([stockid])
REFERENCES [dbo].[stockinformation] ([id])
GO
ALTER TABLE [dbo].[stockbooklist] CHECK CONSTRAINT [fk_stockid_pkey]
GO
ALTER TABLE [dbo].[stockinformation]  WITH CHECK ADD  CONSTRAINT [fk_company_pkey] FOREIGN KEY([companyid])
REFERENCES [dbo].[company] ([id])
GO
ALTER TABLE [dbo].[stockinformation] CHECK CONSTRAINT [fk_company_pkey]
GO
ALTER TABLE [dbo].[transactions]  WITH CHECK ADD  CONSTRAINT [fk_broker_transaction_pkey] FOREIGN KEY([brokerid])
REFERENCES [dbo].[broker] ([id])
GO
ALTER TABLE [dbo].[transactions] CHECK CONSTRAINT [fk_broker_transaction_pkey]
GO
ALTER TABLE [dbo].[transactions]  WITH CHECK ADD  CONSTRAINT [fk_forclientid_pkey] FOREIGN KEY([forclientid])
REFERENCES [dbo].[client] ([id])
GO
ALTER TABLE [dbo].[transactions] CHECK CONSTRAINT [fk_forclientid_pkey]
GO
ALTER TABLE [dbo].[transactions]  WITH CHECK ADD  CONSTRAINT [fk_forcompanyid_pkey] FOREIGN KEY([forcompanyid])
REFERENCES [dbo].[company] ([id])
GO
ALTER TABLE [dbo].[transactions] CHECK CONSTRAINT [fk_forcompanyid_pkey]
GO
ALTER TABLE [dbo].[transactions]  WITH CHECK ADD  CONSTRAINT [fk_stockinformation_pkey] FOREIGN KEY([stockid])
REFERENCES [dbo].[stockinformation] ([id])
GO
ALTER TABLE [dbo].[transactions] CHECK CONSTRAINT [fk_stockinformation_pkey]
GO
USE [master]
GO
ALTER DATABASE [stockexchange_new] SET  READ_WRITE 
GO
