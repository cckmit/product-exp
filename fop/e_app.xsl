<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="customroot">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simple">
                    <fo:region-body margin-top="1.0cm" margin-bottom="2.9cm" margin-left="2.0cm" margin-right="2.0cm"/>
                    <fo:region-before/>
                    <fo:region-after extent="2.6cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simple">
                <fo:flow flow-name="xsl-region-body">
                    <!--start logo -->
                    <fo:table width="100%" vertical-align="middle" font-family="DBOzoneX" color="#333333"
                              font-size="13pt" font-weight="normal" empty-cells="show">
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
                                        <fo:external-graphic src="makerealchange.png" content-width="1.2in" scaling="non-uniform"/>
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
                    <!--  end of logo -->

                    <fo:table width="100%" vertical-align="middle" font-family="DBOzoneX" color="#333333"
                              font-size="13pt" font-weight="normal" empty-cells="show">
                        <fo:table-column column-width="50%" text-align="left"/>
                        <fo:table-column column-width="50%" text-align="right"/>

                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left" font-size="11pt" number-columns-spanned="2">
                                    <fo:block background-color="#C0C0C0" border="solid 0.2mm black">รายละเอียดการขอสินเชื่อ</fo:block>
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
                              start-indent="0cm" margin-top="-0.5cm">
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
                                        <xsl:value-of select="citizenId"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>วงเงินกู้ :</fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="finalLoanAmount"/>
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

                            <!-- For Cash2Go and Cash2Go TopUp -->
                            <xsl:if test="productCode='C2G01' or productCode='C2G02'">
                                <xsl:if test="count(interestRateDS) >= 1">
                                    <fo:table-row>
                                        <fo:table-cell text-align="left" number-columns-spanned="2">
                                            <fo:block>
                                                <fo:leader leader-pattern="space"/>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell text-align="left" number-columns-spanned="2">
                                            <fo:block>อัตราดอกเบี้ย :</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <xsl:for-each select="interestRateDS">
                                        <fo:table-row>
                                            <fo:table-cell text-align="left" number-columns-spanned="2">
                                                <fo:block>
                                                    <xsl:value-of select="interestRate"/>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>

                                    <fo:table-row>
                                        <fo:table-cell text-align="left" number-columns-spanned="2">
                                            <fo:block>
                                                <xsl:value-of select="rateTypeValue"/>
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
                                </xsl:if>
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
                            </xsl:if>

                            <!-- For Ready Cash -->

                            <xsl:if test="productCode='RC01' and featureType=''">
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


                            <xsl:if test="productCode='RC01' and featureType='C'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>อัตราดอกเบี้ย :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block><xsl:value-of select="interestRate"/>%
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <!-- For Cash2Go TopUp -->
                            <xsl:if test="productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ยอดเงินกู้ที่โอนเข้าบัญชี :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block><xsl:value-of select="outstandingBal"/>บาท
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ยอดเงินกู้ที่นำไปชำระหนี้ เดิม:</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block><xsl:value-of select="existingOsBal"/>บาท (บัญชีเงินกู้เลขที่
                                            <xsl:value-of select="existingAcctNo"/>)
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <!-- For Cash2Go and Cash2Go TopUp -->
                            <xsl:if test=" productCode='C2G01' or productCode='C2G02' ">
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
                            <!-- For Cash2Go and Cash2Go TopUp -->
                            <xsl:if test=" productCode='C2G01' or productCode='C2G02' ">
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
                                        <fo:block>ยอดเงินที่ต้องชำระรายเดือน :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="installmentAmount"/> บาท
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>วิธีการชำระเงิน :</fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:choose>
                                            <xsl:when test="paymentMethod='0'">
                                                เงินสด
                                            </xsl:when>
                                            <xsl:otherwise>
                                                หักจากบัญชีธนาคาร ทีทีบี
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>E-statement :</fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="eStatement"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>


                            <xsl:if test=" showBOTFields='Y' ">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>สินเชื่อฯ กับสถาบันการเงินอื่น :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="loanWithOtherBank"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>สินเชื่อฯ ที่อยู่ระหว่างการพิจารณา :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="considerLoanWithOtherBank"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>


                            <xsl:if test="productCode='RC01' and featureType='S'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ระยะเวลาผ่อนชำระตามสัญญา(เดือน) :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="tenureFeat"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>
                            <xsl:if test="productCode='RC01' and requestAmount!='[-]'">
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
                                        ข้าพเจ้าขอยืนยันต่อทางธนาคารว่าข้อความที่ให้ไว้ในการสมัครสินเชื่อเป็นจริงและสมบูรณ์
                                        ข้าพเจ้าอนุญาตให้ธนาคารเรียกหลักฐานการเงิน เพิ่มเติม
                                        เพื่อวัตถุประสงค์ในการประเมินวงเงินสินเชื่อ
                                        ข้าพเจ้ารับทราบว่าใบสมัครของข้าพเจ้าจะเป็นที่ยอมรับได้ก็ต่อเมื่อได้รับการอนุมัติเป็นลายลักษณ์
                                        อักษรจากธนาคารแล้ว
                                        โดยข้าพเจ้ายินยอมผูกพันและตกลงปฏิบัติตามข้อกำหนดและเงื่อนไขของสัญญาให้สินเชื่อที่ระบุไว้ด้านบนคำขอนี้ทุกประการ
                                        ทั้งที่มีอยู่ ในปัจจุบันและที่จะมีขึ้นในอนาคต รวมตลอดถึงข้อกำหนด และ เงื่อนไข
                                        เพิ่มเติมใดๆ ที่ธนาคารจะได้แจ้งให้ทราบเป็นครั้งคราวในกรณีที่ธนาคาร
                                        ไม่สามารถอนุมัติจำนวนเงินกู้ และ/หรือระยะเวลาการผ่อน ชำระ
                                        คืนได้ตามความประสงค์ของข้าพเจ้าที่ระบุไว้ในใบสมัครนี้ข้าพเจ้ายังประสงค์และตกลง
                                        ผูกพันตามจำนวนเงินกู้
                                        และ/หรือระยะเวลาการผ่อนชำระคืนตลอดจนข้อกำหนดและเงื่อนไขที่ธนาคารได้อนุมัติให้ข้าพเจ้าทุกประการ
                                        ข้าพเจ้าได้อ่าน และเข้าใจข้อกำหนด และเงื่อนไขต่างๆ ที่เกี่ยวข้องอย่างถี่ถ้วนแล้ว
                                        จึงลงลายมือชื่อไว้เป็นหลักฐานแห่งการกู้ยืม และข้าพเจ้าได้รับสำเนาสัญญาให้
                                        สินเชื่อจากธนาคารไว้เรียบร้อยแล้ว
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        <xsl:choose>
                                            <xsl:when test="isReject='Y'">
                                                ข้าพเจ้าขอรับรองว่าข้อมูลที่ให้ไว้ในการสมัครสินเชื่อนี้เป็นความจริงทุกประการ
                                                และในกรณีที่ข้าพเจ้าได้ตกลงให้บริษัทข้อมูลเครดิตแห่งชาติ จำกัด (บริษัท)
                                                เปิดเผยหรือให้ข้อมูลของข้าพเจ้าแก่ธนาคารทหารไทยธนชาต จำกัด (มหาชน)
                                                ซึ่งเป็นสมาชิกหรือผู้ใช้บริการของบริษัทนั้นเพื่อประโยชน์ในการ
                                                วิเคราะห์สินเชื่อ การออกบัตรเครดิต
                                                ตามคำขอสินเชื่อ/ขอออกบัตรเครดิตของข้าพเจ้ารวมทั้งเพื่อประโยชน์ในการทบทวนสินเชื่อต่ออายุสัญญา
                                                สินเชื่อ/บัตรเครดิต
                                                การบริหารและป้องกันความเสี่ยงตามข้อกำหนดของธนาคารแห่งประเทศไทย
                                                และให้ถือว่าคู่ฉบับและบรรดาสำเนาภาพถ่าย
                                                ข้อมูลอิเล็กทรอนิกส์หรือโทรสารที่ทำสำเนาขึ้น
                                                จากหนังสือให้ความยินยอมฉบับนี้ โดยการถ่ายสำเนาถ่ายภาพ
                                                หรือบันทึกไว้ไม่ว่าในรูปแบบใดๆ
                                                เป็นหลักฐานในการให้ความยินยอมของข้าพเจ้าเช่นเดียวกัน
                                                จนกว่าข้าพเจ้าจะแจ้งยกเลิกหรือเพิกถอนความยินยอมนี้เป็นลายลักษณ์อักษร
                                                ต่อธนาคาร
                                                รวมถึงยอมรับผูกพันปฏิบัติตามข้อกำหนดและเงื่อนไขการใช้บัตรเครดิตของธนาคาร
                                                หรือกฎระเบียบข้อบังคับการเป็นสมาชิกบัตร เครดิตของธนาคารที่ได้รับ
                                                พร้อมบัตร เครดิตของธนาคารรวมทั้งที่แก้ไขเพิ่มเติมต่อไปด้วย
                                            </xsl:when>
                                            <xsl:otherwise>
                                                ข้าพเจ้าขอรับรองว่าข้อมูลที่ให้ไว้ในการสมัครสินเชื่อนี้เป็นความจริงทุกประการ
                                                และตกลงให้บริษัทข้อมูลเครดิตแห่งชาติ จำกัด (บริษัท)
                                                เปิดเผยหรือให้ข้อมูลของข้าพเจ้าแก่ธนาคารทหารไทยธนชาต จำกัด (มหาชน)
                                                ซึ่งเป็นสมาชิกหรือผู้ใช้บริการของบริษัทเพื่อประโยชน์ในการวิเคราะห์สินเชื่อ
                                                การออกบัตรเครดิต ตามคำขอสินเชื่อ/ขอออกบัตรเครดิตของข้าพเจ้า
                                                รวมทั้งเพื่อประโยชน์ในการทบทวนสินเชื่อต่ออายุสัญญาสินเชื่อ/บัตรเครดิต
                                                การบริหารและป้องกันความเสี่ยง ตามข้อกำหนดของธนาคารแห่งประเทศไทย
                                                และให้ถือว่าคู่ฉบับ และบรรดาสำเนาภาพถ่ายข้อมูล
                                                อิเล็กทรอนิกส์หรือโทรสารที่ทำสำเนาขึ้นจากหนังสือให้ความยินยอมฉบับนี้
                                                โดยการถ่ายสำเนา ถ่ายภาพ หรือบันทึกไว้ไม่ว่าในรูปแบบใดๆ
                                                เป็นหลักฐานในการให้ความยินยอมของข้าพเจ้าเช่นเดียวกัน
                                                จนกว่าข้าพเจ้าจะแจ้งยกเลิก หรือเพิกถอนความยินยอมนี้เป็นลายลักษณ์อักษรต่อ
                                                ธนาคาร
                                                รวมถึงยอมรับผูกพันปฏิบัติตามข้อกำหนดและเงื่อนไขการใช้บัตรเครดิตของธนาคาร
                                                หรือ กฏระเบียบข้อบังคับการเป็นสมาชิกบัตรเครดิต ของธนาคารที่ได้รับ
                                                พร้อมบัตรเครดิตของธนาคารรวมทั้งที่แก้ไขเพิ่มเติมต่อไปด้วย
                                            </xsl:otherwise>
                                        </xsl:choose>

                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <xsl:if test="paymentMethod='1'">
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-indent="1cm">
                                            <fo:inline>กรณีที่ข้าพเจ้าเลือก ชำระด้วยการหักบัญชีเงินฝาก ttb บนใบสมัครนี้
                                                หมายถึง ยินยอมให้ธนาคารหักเงินฝากจากบัญชีเงินฝากออมทรัพย์ เลขที่
                                            </fo:inline>
                                            <fo:inline>
                                                <xsl:value-of select="directDebitAcct"/>
                                            </fo:inline>
                                            <fo:inline>
                                                ดังกล่าวเพื่อชำระหนี้หรือค่าธรรมเนียมหรือค่าใช้จ่ายอื่นใดที่เกิดจากการใช้บริการสินเชื่อ
                                                โดยไม่ต้องขอความยินยอมจากข้าพเจ้าแต่อย่างใด
                                            </fo:inline>
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
                                            ผู้สมัครให้ความยินยอมโดย : Access Pin
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
                            </xsl:if>
                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        ผู้สมัครให้ความยินยอมโดย : Access Pin
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
                                        ให้ ธนาคารทหารไทยธนชาต จำกัด (มหาชน)ซึ่งเป็นสมาชิก ของบริษัทข้อมูลเครดิต
                                        นำข้อมูลของข้าพเจ้าที่ได้รับจากบริษัทข้อมูลเครดิตเฉพาะส่วนที่ไม่สามารถระบุตัวตน
                                        เช่น ชื่อ นามสกุลเลขประจำตัว ประชาชน
                                        รวมถึงข้อมูลอื่นใดที่สามารถระบุว่าเป็นข้าพเจ้าได้
                                        ไปใช้เป็นปัจจัยหนึ่งในการจัดทำแบบจำลองด้านเครดิตกำหนดเท่านั้น และให้คู่ฉบับ
                                        และบรรดาสำเนาภาพถ่าย ข้อมูลอิเล็กทรอนิกส์ หรือ โทรสาร
                                        ที่ได้ทำซ้ำขึ้นจากหนังสือให้ความยินยอมฉบับนี้โดยการถ่ายสำเนา ภาพถ่าย
                                        หรือบันทึกไว้ไม่ว่าในรูปแบบใดๆ
                                        ให้ถือเป็นหลักฐานในการให้ความยินยอมของข้าพเจ้าเช่นเดียวกัน
                                        ทั้งนี้ข้าพเจ้าทราบว่าเจ้าของข้อมูลมีสิทธิที่จะ
                                        ให้ความยินยอมหรือไม่ก็ได้และเมื่อให้ความยินยอมแล้วเจ้าของข้อมูลจะแจ้งความประสงค์ไม่ให้ความยินยอมอีกต่อไปได้
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
