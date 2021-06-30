<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="customroot">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simple">
                    <fo:region-body region-name="xsl-region-body" margin-top="3.0cm" margin-bottom="3.0cm"
                                    margin-left="2.0cm" margin-right="2.0cm"/>
                    <fo:region-before region-name="xsl-region-before"/>
                    <fo:region-after region-name="xsl-region-after" extent="3.5cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simple">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:table width="100%" margin-top="1.0cm" margin-left="0.5cm" margin-right="1.5cm">
                        <fo:table-column column-width="50%" text-align="left"/>
                        <fo:table-column column-width="50%" text-align="right"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-align="left">
                                        <fo:external-graphic src="tmblogo.png" content-width="2.00in"
                                                             scaling="non-uniform"/>
                                    </fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block text-align="right">
                                        <fo:external-graphic src="makerealchange.png" content-width="1.2in"
                                                             scaling="non-uniform"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left" number-columns-spanned="2">
                                    <fo:block>
                                        <fo:leader leader-pattern="space"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:static-content>
                <fo:static-content flow-name="xsl-region-after">
                    <fo:table width="100%" margin-left="0.0cm" margin-right="1.0cm">
                        <fo:table-column column-width="100%" text-align="left"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-align="left">
                                        <fo:external-graphic src="tmbaddress.png"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:table width="100%" vertical-align="middle" font-family="DBOzoneX" color="#333333"
                              font-size="13pt" font-weight="normal" empty-cells="show">
                        <fo:table-column column-width="50%" text-align="left"/>
                        <fo:table-column column-width="50%" text-align="right"/>

                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left" font-size="11pt" number-columns-spanned="2">
                                    <fo:block background-color="#C0C0C0" border="solid 0.2mm black">
                                        รายละเอียดการขอสินเชื่อ
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left" number-columns-spanned="2">
                                    <fo:block>
                                        <fo:leader leader-pattern="space"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                        </fo:table-body>
                    </fo:table>


                    <fo:table width="100%" vertical-align="middle" font-family="DBOzoneX"
                              color="#333333" font-size="13pt" font-weight="normal" empty-cells="show"
                              start-indent="0cm" margin-top="-1.0cm">
                        <fo:table-column column-width="50%" text-align="right"/>
                        <fo:table-column column-width="50%" text-align="left"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left" number-columns-spanned="2">
                                    <fo:block>
                                        <fo:leader leader-pattern="space"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>ใบสมัครเลขที่ :</fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="appRefNo"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>ผลิตภัณฑ์ที่ต้องการสมัคร :</fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="productName"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>ชื่อ - นามสกุล :</fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="customerName"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>เลขที่บัตรประชาชน :</fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="idCardNo"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>
                                        <xsl:choose>
                                            <xsl:when test="productCode='C2G01' or productCode='C2G02'">
                                                วงเงินกู้ :
                                            </xsl:when>
                                            <xsl:otherwise>
                                                วงเงินบัตร :
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="finalLoanAmount"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <xsl:if test="productCode='RC01'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ระยะเวลาการผ่อนชำระ :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="tenor"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <xsl:if test="productCode='C2G01' or productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell text-align="left" number-columns-spanned="2">
                                        <fo:block>
                                            <fo:leader leader-pattern="space"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <xsl:if test="productCode='RC01' or productCode='C2G01' or productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>อัตราดอกเบี้ย :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="interestRate"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <xsl:if test="productCode='C2G01' or productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell text-align="left" number-columns-spanned="2">
                                        <fo:block>
                                            <fo:leader leader-pattern="space"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <xsl:if test="productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ยอดเงินที่จะได้รับโอนเข้าบัญชี :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="cashDisbursement"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <xsl:if test="productCode='C2G01' or productCode='C2G02'">

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>บัญชีรับเงิน :</fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="disburseAccountNo"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ระยะเวลาผ่อนชำระตามสัญญา :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="tenor"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                            </xsl:if>

                            <xsl:if test="productCode='C2G02'">
                                <fo:table-row>

                                    <fo:table-cell>
                                        <fo:block>ยอดเงินกู้ที่นำไปชำระหนี้เดิม :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="currentLoan"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>บัญชีเงินกู้เลขที่ :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="currentAccount"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                            </xsl:if>

                            <xsl:if test="productCode='RC01'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>จำนวนเงินที่ต้องการขอเบิกใช้ :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="requestAmount"/> บาท
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>วันที่ทำสัญญา :</fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="applyDate"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <xsl:if test="productCode='C2G01' or productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>วันครบรอบกาหนดชำระ :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="dueDate"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>เริ่มชำระงวดแรก :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="firstPaymentDueDate"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>รอบชำระเงินถัดไป :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="nextPaymentDueDate"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ยอดเงินท่ีต้องชำระรายเดือน :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="installment"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>
                                        <xsl:choose>
                                            <xsl:when test="productCode='C2G01' or productCode='C2G02'">
                                                วิธีการผ่อนชำระเงิน :
                                            </xsl:when>
                                            <xsl:otherwise>
                                                วิธีการชำระเงิน :
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="paymentMethod"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>Estatement :</fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="email"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <xsl:if test="productCode='RC01' or productCode='C2G01' or productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>สินเชื่อฯ กับสถาบันการเงินอื่น :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="botAnswer1"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>สินเชื่อฯ ที่อยู่ระหว่างการพิจารณา :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="botAnswer2"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>
                            <fo:table-row>
                                <fo:table-cell text-align="left" number-columns-spanned="2">
                                    <fo:block>
                                        <fo:leader leader-pattern="space"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <!--                            &lt;!&ndash; For Cash2Go and Cash2Go TopUp &ndash;&gt;-->
                            <!--                            <xsl:if test="productCode='C2G01' or productCode='C2G02'">-->
                            <!--                                <xsl:if test="count(interestRateDS) >= 1">-->
                            <!--                                    <fo:table-row>-->
                            <!--                                        <fo:table-cell text-align="left" number-columns-spanned="2">-->
                            <!--                                            <fo:block>-->
                            <!--                                                <fo:leader leader-pattern="space"/>-->
                            <!--                                            </fo:block>-->
                            <!--                                        </fo:table-cell>-->
                            <!--                                    </fo:table-row>-->

                            <!--                                    <fo:table-row>-->
                            <!--                                        <fo:table-cell text-align="left" number-columns-spanned="2">-->
                            <!--                                            <fo:block>อัตราดอกเบี้ย :</fo:block>-->
                            <!--                                        </fo:table-cell>-->
                            <!--                                    </fo:table-row>-->

                            <!--                                    <xsl:for-each select="interestRateDS">-->
                            <!--                                        <fo:table-row>-->
                            <!--                                            <fo:table-cell text-align="left" number-columns-spanned="2">-->
                            <!--                                                <fo:block>-->
                            <!--                                                    <xsl:value-of select="interestRate"/>-->
                            <!--                                                </fo:block>-->
                            <!--                                            </fo:table-cell>-->
                            <!--                                        </fo:table-row>-->
                            <!--                                    </xsl:for-each>-->

                            <!--                                    <fo:table-row>-->
                            <!--                                        <fo:table-cell text-align="left" number-columns-spanned="2">-->
                            <!--                                            <fo:block>-->
                            <!--                                                <xsl:value-of select="rateTypeValue"/>-->
                            <!--                                            </fo:block>-->
                            <!--                                        </fo:table-cell>-->
                            <!--                                    </fo:table-row>-->

                            <!--                                    <fo:table-row>-->
                            <!--                                        <fo:table-cell text-align="left" number-columns-spanned="2">-->
                            <!--                                            <fo:block>-->
                            <!--                                                <fo:leader leader-pattern="space"/>-->
                            <!--                                            </fo:block>-->
                            <!--                                        </fo:table-cell>-->
                            <!--                                    </fo:table-row>-->
                            <!--                                </xsl:if>-->
                            <!--                                <fo:table-row>-->
                            <!--                                    <fo:table-cell>-->
                            <!--                                        <fo:block>บัญชีรับเงิน :</fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                    <fo:table-cell text-align="right">-->
                            <!--                                        <fo:block>-->
                            <!--                                            <xsl:value-of select="disburseAccountNo"/>-->
                            <!--                                        </fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                </fo:table-row>-->
                            <!--                            </xsl:if>-->

                            <!--                            &lt;!&ndash; For Ready Cash &ndash;&gt;-->

                            <!--                            <xsl:if test="productCode='RC01' and featureType=''">-->
                            <!--                                <fo:table-row>-->
                            <!--                                    <fo:table-cell>-->
                            <!--                                        <fo:block>อัตราดอกเบี้ย :</fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                    <fo:table-cell text-align="right">-->
                            <!--                                        <fo:block>-->
                            <!--                                            <xsl:value-of select="interestRate"/>-->
                            <!--                                        </fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                </fo:table-row>-->
                            <!--                            </xsl:if>-->


                            <!--                            <xsl:if test="productCode='RC01' and featureType='C'">-->
                            <!--                                <fo:table-row>-->
                            <!--                                    <fo:table-cell>-->
                            <!--                                        <fo:block>อัตราดอกเบี้ย :</fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                    <fo:table-cell text-align="right">-->
                            <!--                                        <fo:block><xsl:value-of select="interestRate"/>%-->
                            <!--                                        </fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                </fo:table-row>-->
                            <!--                            </xsl:if>-->

                            <!--                            &lt;!&ndash; For Cash2Go TopUp &ndash;&gt;-->
                            <!--                            <xsl:if test="productCode='C2G02'">-->
                            <!--                                <fo:table-row>-->
                            <!--                                    <fo:table-cell>-->
                            <!--                                        <fo:block>ยอดเงินกู้ที่โอนเข้าบัญชี :</fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                    <fo:table-cell text-align="right">-->
                            <!--                                        <fo:block><xsl:value-of select="outstandingBal"/>บาท-->
                            <!--                                        </fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                </fo:table-row>-->
                            <!--                                <fo:table-row>-->
                            <!--                                    <fo:table-cell>-->
                            <!--                                        <fo:block>ยอดเงินกู้ที่นำไปชำระหนี้ เดิม:</fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                    <fo:table-cell text-align="right">-->
                            <!--                                        <fo:block><xsl:value-of select="existingOsBal"/>บาท (บัญชีเงินกู้เลขที่-->
                            <!--                                            <xsl:value-of select="existingAcctNo"/>)-->
                            <!--                                        </fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                </fo:table-row>-->
                            <!--                            </xsl:if>-->


                            <!-- For Ready Cash and Credit Card -->
                            <!-- <xsl:if test=" productCode!='C2G01' and productCode!='C2G02' and productCode!='RC01'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>วันตัดรอบบัญชี : </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block><xsl:value-of select="cycleCutDate" /></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>
                            <xsl:if test="productCode!='RC01'">
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>วันครบกำหนดชำระ : </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right">
                                    <fo:block><xsl:value-of select="firstPaymentDueDateDD" /></fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            </xsl:if>
                            <xsl:if test="productCode='RC01' and featureType =''">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>วันตัดรอบบัญชี : </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block><xsl:value-of select="cycleCutDate" /></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>
                             <xsl:if test="productCode='RC01' and featureType =''">
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>วันครบกำหนดชำระ : </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right">
                                    <fo:block><xsl:value-of select="firstPaymentDueDateDD" /></fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            </xsl:if> -->


                            <!--                            <xsl:if test=" showBOTFields='Y' ">-->
                            <!--                                <fo:table-row>-->
                            <!--                                    <fo:table-cell>-->
                            <!--                                        <fo:block>สินเชื่อฯ กับสถาบันการเงินอื่น :</fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                    <fo:table-cell text-align="right">-->
                            <!--                                        <fo:block>-->
                            <!--                                            <xsl:value-of select="loanWithOtherBank"/>-->
                            <!--                                        </fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                </fo:table-row>-->
                            <!--                                <fo:table-row>-->
                            <!--                                    <fo:table-cell>-->
                            <!--                                        <fo:block>สินเชื่อฯ ที่อยู่ระหว่างการพิจารณา :</fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                    <fo:table-cell text-align="right">-->
                            <!--                                        <fo:block>-->
                            <!--                                            <xsl:value-of select="considerLoanWithOtherBank"/>-->
                            <!--                                        </fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                </fo:table-row>-->
                            <!--                            </xsl:if>-->


                            <!--                            <xsl:if test="productCode='RC01' and featureType='S'">-->
                            <!--                                <fo:table-row>-->
                            <!--                                    <fo:table-cell>-->
                            <!--                                        <fo:block>ระยะเวลาผ่อนชำระตามสัญญา(เดือน) :</fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                    <fo:table-cell text-align="right">-->
                            <!--                                        <fo:block>-->
                            <!--                                            <xsl:value-of select="tenureFeat"/>-->
                            <!--                                        </fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                </fo:table-row>-->
                            <!--                            </xsl:if>-->
                            <!--                            <xsl:if test="productCode='RC01' and requestAmount!='[-]'">-->
                            <!--                                <fo:table-row>-->
                            <!--                                    <fo:table-cell>-->
                            <!--                                        <fo:block>จำนวนเงินที่ต้องการขอเบิกใช้ :</fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                    <fo:table-cell text-align="right">-->
                            <!--                                        <fo:block>-->
                            <!--                                            <xsl:value-of select="requestAmount"/> บาท-->
                            <!--                                        </fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                </fo:table-row>-->
                            <!--                            </xsl:if>-->

                        </fo:table-body>
                    </fo:table>

                    <fo:table width="100%" vertical-align="middle" font-family="DBOzoneX" color="#333333"
                              font-size="13pt" font-weight="normal" empty-cells="show">
                        <fo:table-body>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block>
                                        <fo:leader leader-pattern="space"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        ข้าพเจ้าขอยืนยันต่อธนาคารว่าข้อมูลที่ข้าพเจ้าให้ไว้ต่อธนาคารเป็นความจริงทุกประการและข้าพเจ้าอนุญาตให้ธนาคารเรียก
                                        หลักฐานการเงินเพิ่มเติมเพื่อวัตถุประสงค์ในการประเมินวงเงินสินเชื่อ
                                        ข้าพเจ้ารับทราบว่าการสมัครของข้าพเจ้าจะเป็นที่ยอมรับได้
                                        ต่อเมื่อได้รับการอนุมัติเป็นลายลักษณ์อักษรจากกธนาคารแล้ว
                                        โดยข้าพเจ้ายินยอมผูกพันและตกลงปฏิบัติตามข้อกำหนดและเงื่อนไขของ
                                        สินเชื่อบุคคลแคชทูโกที่ระบุไว้ทุกประการ
                                        รวมตลอดถึงข้อกำหนดและเงื่อนไขเพิ่มเติมที่ธนาคารจะได้แจ้งให้ทราบเป็นครั้งคราว
                                        ในกรณีที่
                                        ธนาคารไม่สามารถอนุมัติจำนวนเงินกู้และระยะเวลาการผ่อนชำระคืนได้ตามความประสงค์ของข้าพเจ้าที่ระบุในคำขอนี้
                                        ข้าพเจ้ายังประสงค์
                                        และตกลงผูกพันตามจำนวนเงินกู้และระยะเวลาการผ่อนชำระคืน
                                        ตลอดจนข้อกำหนดและเงื่อนไขที่ธนาคารได้อนุมัติให้ข้าพเจ้าทุกประการ
                                        ข้าพเจ้าได้อ่านและเข้าใจข้อกำหนดและเงื่อนไขที่เกี่ยวข้องอย่างถี่ถ้วนแล้ว
                                        จึงลงลายมือชื่ออิเล็กทรอนิกส์ไว้เป็นหลักฐานแห่งการกู้ยืม
                                        และข้าพเจ้ารับทราบว่าธนาคารจะจัดส่งสำเนาสัญญาสินเชื่อให้แก่ข้าพเจ้าผ่านทางจดหมายอิเล็กทรอนิกส์
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        ข้าพเจ้าขอรับรองว่าข้อมูลที่ให้ไว้ในการสมัครสินเชื่อนี้เป็นความจริงทุกประการ
                                        และตกลงให้บริษัทข้อมูลเครดิตแห่งชาติ จำกัด (บริษัท) เปิดเผยหรือ
                                        ให้ข้อมูลของข้าพเจ้าแก่ธนาคารทหารไทย จำกัด (มหาชน)
                                        ซึ่งเป็นสมาชิกหรือผู้ใช้บริการของบริษัทเพื่อประโยชน์ในการวิเคราะห์สินเชื่อ
                                        การออก
                                        บัตรเครดิต ตามคำขอสินเชื่อ/ขอออกบัตรเครดิตของข้าพเจ้า
                                        รวมทั้งเพื่อประโยชน์ในการทบทวนสินเชื่อต่ออายุสัญญาสินเชื่อ/บัตรเครดิต การบริหาร
                                        และป้องกันความเสี่ยง ตามข้อกำหนดของธนาคารแห่งประเทศไทย และให้ถือว่าคู่ฉบับ
                                        และบรรดาสำเนาภาพถ่าย ข้อมูลอิเล็กทรอนิกส์หรือโทรสารที่
                                        ทำสำเนาขึ้นจากหนังสือให้ความยินยอมฉบับนี้ โดยการถ่ายสำเนา ถ่ายภาพ
                                        หรือบันทึกไว้ไม่ว่าในรูปแบบใดๆ เป็นหลักฐานในการให้ความยินยอมของ
                                        ข้าพเจ้าเช่นเดียวกัน จนกว่าข้าพเจ้าจะแจ้งยกเลิก
                                        หรือเพิกถอนความยินยอมนี้เป็นลายลักษณ์อักษรต่อธนาคาร
                                        รวมถึงยอมรับผูกพันปฏิบัติตามข้อกำหนด
                                        และเงื่อนไขการใช้บัตรเครดิตของธนาคาร หรือ กฏระเบียบข้อบังคับ
                                        การเป็นสมาชิกบัตรเครดิตของธนาคารที่ได้รับ พร้อมบัตรเครดิตของธนาคารรวม
                                        ทั้งที่แก้ไขเพิ่มเติมต่อไปด้วย
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        กรณีที่ข้าพเจ้าเลือก ชำระด้วยการหักบัญชีเงินฝาก TMB บนใบสมัครนี้ หมายถึง
                                        ยินยอมให้ธนาคารหักเงินฝากจากบัญชีเงินฝากออมทรัพย์
                                        เลขที่
                                        <xsl:value-of select="disburseAccountNo"/> [TMB Direct Debit Account value]
                                        ดังกล่าวเพื่อชำระหนี้หรือค่าธรรมเนียมหรือค่าใช้จ่ายอื่นใดที่เกิดจากการใช้บริการ
                                        สินเชื่อ โดยไม่ต้องขอความยินยอมจากข้าพเจ้า
                                        และธนาคารไม่จำเป็นต้องแจ้งการหักบัญชีแก่ข้าพเจ้าแต่อย่างใด
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <!--                            <fo:table-row>-->
                            <!--                                <fo:table-cell text-align="left">-->
                            <!--                                    <fo:block text-indent="1cm">-->
                            <!--                                        <xsl:choose>-->
                            <!--                                            <xsl:when test="isReject='Y'">-->
                            <!--                                                ข้าพเจ้าขอรับรองว่าข้อมูลที่ให้ไว้ในการสมัครสินเชื่อนี้เป็นความจริงทุกประการ และตกลงให้บริษัท-->
                            <!--                                                ข้อมูลเครดิตแห่งชาติ จำกัด (บริษัท) เปิดเผยหรือให้ข้อมูลของข้าพเจ้าแก่ธนาคารทหารไทย-->
                            <!--                                                จำกัด (มหาชน) ซึ่งเป็นสมาชิกหรือผู้ใช้บริการของบริษัทเพื่อประโยชน์ในการวิเคราะห์สินเชื่อ-->
                            <!--                                                การออกบัตรเครดิต ตามคำขอสินเชื่อ/ขอออกบัตรเครดิตของข้าพเจ้า รวมทั้งเพื่อประโยชน์ในการ-->
                            <!--                                                ทบทวนสินเชื่อต่ออายุสัญญาสินเชื่อ/บัตรเครดิต การบริหารและป้องกันความเสี่ยง ตามข้อกำหนดของ-->
                            <!--                                                ธนาคารแห่งประเทศไทย และให้ถือว่าคู่ฉบับ และบรรดาสำเนาภาพถ่าย ข้อมูลอิเล็กทรอนิกส์หรือ-->
                            <!--                                                โทรสารที่ทำสำเนาขึ้นจากหนังสือให้ความยินยอมฉบับนี้ โดยการถ่ายสำเนา ถ่ายภาพ หรือบันทึกไว้-->
                            <!--                                                ไม่ว่าในรูปแบบใดๆ เป็นหลักฐานในการให้ความยินยอมของข้าพเจ้าเช่นเดียวกัน จนกว่าข้าพเจ้าจะ-->
                            <!--                                                แจ้งยกเลิก หรือเพิกถอนความยินยอมนี้เป็นลายลักษณ์อักษรต่อธนาคาร รวมถึงยอมรับผูกพันปฏิบัติตาม-->
                            <!--                                                ข้อกำหนดและเงื่อนไขการใช้บัตรเครดิตของธนาคาร หรือ กฏระเบียบข้อบังคับ การเป็นสมาชิกบัตร-->
                            <!--                                                เครดิตของธนาคารที่ได้รับ พร้อมบัตรเครดิตของธนาคารรวมทั้งที่แก้ไขเพิ่มเติมต่อไปด้วย-->
                            <!--                                            </xsl:when>-->
                            <!--                                            <xsl:otherwise>-->
                            <!--                                                ข้าพเจ้าขอรับรองว่าข้อมูลที่ให้ไว้ในการสมัครสินเชื่อนี้เป็นความจริงทุกประการ-->
                            <!--                                                และตกลงให้บริษัทข้อมูลเครดิตแห่งชาติ จำกัด (บริษัท)-->
                            <!--                                                เปิดเผยหรือให้ข้อมูลของข้าพเจ้าแก่ธนาคารทหารไทยธนชาต จำกัด (มหาชน)-->
                            <!--                                                ซึ่งเป็นสมาชิกหรือผู้ใช้บริการของบริษัทเพื่อประโยชน์ในการวิเคราะห์สินเชื่อ-->
                            <!--                                                การออกบัตรเครดิต ตามคำขอสินเชื่อ/ขอออกบัตรเครดิตของข้าพเจ้า-->
                            <!--                                                รวมทั้งเพื่อประโยชน์ในการทบทวนสินเชื่อต่ออายุสัญญาสินเชื่อ/บัตรเครดิต-->
                            <!--                                                การบริหารและป้องกันความเสี่ยง ตามข้อกำหนดของธนาคารแห่งประเทศไทย-->
                            <!--                                                และให้ถือว่าคู่ฉบับ และบรรดาสำเนาภาพถ่ายข้อมูล-->
                            <!--                                                อิเล็กทรอนิกส์หรือโทรสารที่ทำสำเนาขึ้นจากหนังสือให้ความยินยอมฉบับนี้-->
                            <!--                                                โดยการถ่ายสำเนา ถ่ายภาพ หรือบันทึกไว้ไม่ว่าในรูปแบบใดๆ-->
                            <!--                                                เป็นหลักฐานในการให้ความยินยอมของข้าพเจ้าเช่นเดียวกัน-->
                            <!--                                                จนกว่าข้าพเจ้าจะแจ้งยกเลิก หรือเพิกถอนความยินยอมนี้เป็นลายลักษณ์อักษรต่อ-->
                            <!--                                                ธนาคาร-->
                            <!--                                                รวมถึงยอมรับผูกพันปฏิบัติตามข้อกำหนดและเงื่อนไขการใช้บัตรเครดิตของธนาคาร-->
                            <!--                                                หรือ กฏระเบียบข้อบังคับการเป็นสมาชิกบัตรเครดิต ของธนาคารที่ได้รับ-->
                            <!--                                                พร้อมบัตรเครดิตของธนาคารรวมทั้งที่แก้ไขเพิ่มเติมต่อไปด้วย-->
                            <!--                                            </xsl:otherwise>-->
                            <!--                                        </xsl:choose>-->

                            <!--                                    </fo:block>-->
                            <!--                                </fo:table-cell>-->
                            <!--                            </fo:table-row>-->
                            <!--                            <xsl:if test="paymentMethod='1'">-->
                            <!--                                <fo:table-row>-->
                            <!--                                    <fo:table-cell text-align="left">-->
                            <!--                                        <fo:block text-indent="1cm">-->
                            <!--                                            <fo:inline>กรณีที่ข้าพเจ้าเลือก ชำระด้วยการหักบัญชีเงินฝาก ttb บนใบสมัครนี้-->
                            <!--                                                หมายถึง ยินยอมให้ธนาคารหักเงินฝากจากบัญชีเงินฝากออมทรัพย์ เลขที่-->
                            <!--                                            </fo:inline>-->
                            <!--                                            <fo:inline>-->
                            <!--                                                <xsl:value-of select="directDebitAcct"/>-->
                            <!--                                            </fo:inline>-->
                            <!--                                            <fo:inline>-->
                            <!--                                                ดังกล่าวเพื่อชำระหนี้หรือค่าธรรมเนียมหรือค่าใช้จ่ายอื่นใดที่เกิดจากการใช้บริการสินเชื่อ-->
                            <!--                                                โดยไม่ต้องขอความยินยอมจากข้าพเจ้าแต่อย่างใด-->
                            <!--                                            </fo:inline>-->
                            <!--                                        </fo:block>-->
                            <!--                                    </fo:table-cell>-->
                            <!--                                </fo:table-row>-->
                            <!--                        </xsl:if>-->
                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        ผู้สมัครให้ความยินยอมโดย : <xsl:value-of select="customerName"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        วันเวลาที่ให้ความยินยอม
                                        <xsl:value-of select="consentDate"/> /
                                        <xsl:value-of select="consentTime"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block>
                                        <fo:leader leader-pattern="space"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        การให้ความยินยอมจากเจ้าของข้อมูลเพื่อใช้จัดทำแบบจำลองด้านเครดิต
                                        <xsl:choose>
                                            <xsl:when test="ncbConsentFlag='Y'">
                                                ยินยอม
                                            </xsl:when>
                                            <xsl:otherwise>
                                                ไม่ยินยอม
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        ให้ ธนาคารทหารไทย จำกัด (มหาชน) ซึ่งเป็นสมาชิกของบริษัทข้อมูลเครดิต
                                        นำข้อมูลของข้าพเจ้าที่ได้รับจากบริษัทข้อมูลเครดิตเฉพาะส่วนที่ไม่สามารถ
                                        ระบุตัวตน เช่น ชื่อ นามสกุล เลขประจำตัวประชาชน
                                        รวมถึงข้อมูลอื่นใดที่สามารถระบุว่าเป็นข้าพเจ้าได้
                                        ไปใช้เป็นปัจจัยหนึ่งในการจัดทำแบบจำลองด้าน
                                        เครดิตกำหนดเท่านั้น และให้คู่ฉบับและบรรดาสำเนาภาพถ่าย ข้อมูลอิเล็กทรอนิกส์ หรือ
                                        โทรสาร ที่ได้ทำซ้ำขึ้นจากหนังสือให้ความยินยอมฉบับนี้ โดยการ
                                        ถ่ายสำเนาภาพถ่าย หรือบันทึกไว้ไม่ว่าในรูปแบบใดๆ
                                        ให้ถือเป็นหลักฐานในการให้ความยินยอมของข้าพเจ้าเช่นเดียวกัน ทั้งนี้
                                        ข้าพเจ้าทราบว่าเจ้าของ
                                        ข้อมูลมีสิทธิที่จะให้ความยินยอมหรือไม่ก็ได้
                                        และเมื่อให้ความยินยอมแล้วเจ้าของข้อมูลจะแจ้งความประสงค์ไม่ให้ความยินยอมอีกต่อไปได้
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block>
                                        <fo:leader leader-pattern="space"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        ผู้สมัครให้ความยินยอมโดย :
                                        <xsl:value-of select="customerName"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        วันเวลาที่ให้ความยินยอม
                                        <xsl:value-of select="ConsentDate"/> /
                                        <xsl:value-of select="ConsentTime"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                        </fo:table-body>
                    </fo:table>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
